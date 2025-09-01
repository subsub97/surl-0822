package com.megastudy.surlkdh.domain.statistics.scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.domain.statistics.scheduler.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncBatchProcessor {

	private static final int BATCH_SIZE = 300;
	private static final String REDIS_STAT_KEY = "shortUrl:stat";
	private static final String FAILED_BATCH_PREFIX = "batch:jobs:failed:";
	private static final Duration FAILED_BATCH_TTL = Duration.ofDays(5);

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	private final StatisticsService statisticsService;

	@Async("batchTaskExecutor") // 위에서 만든 스레드 풀을 사용하도록 지정
	public CompletableFuture<Void> processQueueInParallel(String jobIdentifier) {
		log.info("Async task [{}] started.", jobIdentifier);
		while (true) {

			List<ShortUrlData> batch = getBatch();

			if (batch.isEmpty()) {
				break;
			}

			boolean success = processBatchData(batch, jobIdentifier);

			if (!success) {
				saveFailedBatch(batch, jobIdentifier);
				log.warn("Batch processing failed for [{}]. Stopping this thread. Size: {}", jobIdentifier,
					batch.size());
				break;
			}
		}
		log.info("Async task [{}] finished.", jobIdentifier);
		return CompletableFuture.completedFuture(null);
	}

	private List<ShortUrlData> getBatch() {
		List<ShortUrlData> batch = new ArrayList<>();

		try {
			List<Object> results = redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) {
					for (int i = 0; i < BATCH_SIZE; i++) {
						connection.lPop(redisTemplate.getStringSerializer().serialize(REDIS_STAT_KEY));
					}
					return null;
				}
			});

			batch = results.stream()
				.filter(obj -> obj != null)
				.map(obj -> {
					try {
						return objectMapper.convertValue(obj, ShortUrlData.class);
					} catch (Exception e) {
						log.error("Error converting ShortUrlData: {}", e.getMessage(), e);
						return null;
					}
				})
				.filter(data -> data != null)
				.toList();

		} catch (Exception e) {
			log.error("Error fetching batch from Redis", e);
			// TODO email 또는 slack으로 알리기
		}
		return batch;
	}

	private boolean processBatchData(List<ShortUrlData> batch, String batchTime) {
		try {
			if (batch == null || batch.isEmpty()) {
				return true;
			}

			boolean result = statisticsService.processBatch(batch);

			if (result) {
				log.debug("Batch {} processed successfully with {} items", batchTime, batch.size());
			} else {
				log.warn("Batch {} processing failed. Storing failed batch data.", batchTime);
			}
			return result;
		} catch (DuplicateKeyException e1) {
			log.info("Batch {} contains duplicate data (idempotent operation): {}",
				batchTime, e1.getMessage());
			return true;
		} catch (Exception e2) {
			log.error("Error processing batch {}: {}", batchTime, e2.getMessage(), e2);
			return false;
		}
	}

	private void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
		try {
			String failedKey = FAILED_BATCH_PREFIX + batchTime;

			redisTemplate.opsForList().leftPushAll(failedKey, failedBatch.toArray());

			redisTemplate.expire(failedKey, FAILED_BATCH_TTL);

			log.info("Failed batch saved: {} with {} items, TTL: {} days",
				failedKey, failedBatch.size(), FAILED_BATCH_TTL.toDays());

		} catch (Exception e) {
			log.error("Error saving failed batch {}", batchTime, e);
		}
	}

	//TODO 수동으로 재처리할 수 있도록 api 만들기 + 정상 작동 테스트
	public void retryFailedBatch(String batchTime) {
		String failedKey = FAILED_BATCH_PREFIX + batchTime;

		try {
			// 실패한 배치가 존재하는지 확인
			Long size = redisTemplate.opsForList().size(failedKey);

			if (size == null || size == 0) {
				log.warn("Failed batch not found: {}", failedKey);
				return;
			}

			// 모든 데이터 가져오기
			List<Object> failedJsonData = redisTemplate.opsForList().range(failedKey, 0, -1);

			if (failedJsonData != null && !failedJsonData.isEmpty()) {
				List<ShortUrlData> failedData = failedJsonData.stream()
					.map(json -> {
						try {
							return objectMapper.convertValue(json, ShortUrlData.class);
						} catch (Exception e) {
							log.error("Error converting ShortUrlData during retry: {}", e.getMessage(), e);
							return null;
						}
					})
					.filter(obj -> obj != null)
					.toList();
				int totalChunks = (int)Math.ceil((double)failedData.size() / BATCH_SIZE);

				boolean allSuccess = true;
				for (int i = 0; i < failedData.size(); i += BATCH_SIZE) {
					int endIdx = Math.min(i + BATCH_SIZE, failedData.size());
					List<ShortUrlData> chunk = failedData.subList(i, endIdx);
					int chunkNumber = (i / BATCH_SIZE) + 1;

					log.info("Processing chunk {}/{} with {} items for batch {}",
						chunkNumber, totalChunks, chunk.size(), failedKey);

					boolean chunkSuccess = processBatchData(chunk, batchTime + "_retry_chunk_" + chunkNumber);

					if (!chunkSuccess) {
						log.error("Failed to process chunk {}/{} for batch {}", chunkNumber, totalChunks, failedKey);
						allSuccess = false;
						// 실패해도 다음 청크 계속 처리하고 실패한건 다시 저장
						saveFailedBatch(chunk, batchTime + "_retry_chunk_" + chunkNumber);
					}
				}
				if (allSuccess) {
					// 재처리 성공 시 실패 키 삭제
					redisTemplate.delete(failedKey);
					log.info("Failed batch {} retried successfully and removed", failedKey);
				} else {
					log.warn("Failed batch {} retry failed", failedKey);
				}
			}

		} catch (Exception e) {
			log.error("Error retrying failed batch {}", batchTime, e);
		}
	}
}
