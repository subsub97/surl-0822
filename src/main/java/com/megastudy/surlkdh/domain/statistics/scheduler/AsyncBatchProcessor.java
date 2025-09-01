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
				String failedKey = saveFailedBatch(batch, jobIdentifier);
				log.warn("Batch processing failed for [{}]. Stopping this thread. Size: {}, Saved to: {}", 
					jobIdentifier, batch.size(), failedKey);
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

	private String saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
		String failedKey = FAILED_BATCH_PREFIX + batchTime;
		try {
			redisTemplate.opsForList().leftPushAll(failedKey, failedBatch.toArray());

			redisTemplate.expire(failedKey, FAILED_BATCH_TTL);

			log.info("Failed batch saved: {} with {} items, TTL: {} days",
				failedKey, failedBatch.size(), FAILED_BATCH_TTL.toDays());

			return failedKey;
		} catch (Exception e) {
			log.error("Error saving failed batch {}", batchTime, e);
			return null;
		}
	}

	/**
	 * 실패한 배치를 청크 단위로 재시도 처리
	 * - 청크별로 독립적 처리하여 부분 성공 가능
	 * - 성공한 청크는 즉시 Redis에서 제거
	 * - 실패한 청크만 재저장하여 데이터 손실 방지
	 */
	public RetryResult retryFailedBatch(String batchTime) {
		String failedKey = FAILED_BATCH_PREFIX + batchTime;
		RetryResult result = new RetryResult();
		
		try {
			Long totalSize = redisTemplate.opsForList().size(failedKey);

			if (totalSize == null || totalSize == 0) {
				log.warn("Failed batch not found: {}", failedKey);
				result.setStatus("NOT_FOUND");
				return result;
			}

			log.info("Starting retry for failed batch {} with {} items", failedKey, totalSize);
			
			int processedCount = 0;
			int successfulChunks = 0;
			int failedChunks = 0;
			List<String> failedChunkKeys = new ArrayList<>();

			// 청크 단위로 메모리 효율적 처리
			while (processedCount < totalSize) {
				// 현재 청크를 Redis에서 직접 가져와서 메모리 사용량 최소화
				List<ShortUrlData> chunk = getChunkFromFailedBatch(failedKey, BATCH_SIZE);
				
				if (chunk.isEmpty()) {
					break; // 더 이상 처리할 데이터 없음
				}

				int chunkNumber = (processedCount / BATCH_SIZE) + 1;
				String chunkIdentifier = batchTime + "_retry_chunk_" + chunkNumber;

				log.info("Processing chunk {} with {} items for batch {}",
					chunkNumber, chunk.size(), failedKey);

				boolean chunkSuccess = processBatchData(chunk, chunkIdentifier);

				if (chunkSuccess) {
					successfulChunks++;
					log.debug("Chunk {} processed successfully", chunkNumber);
				} else {
					failedChunks++;
					// 실패한 청크만 새로운 키로 재저장
					String failedChunkKey = saveFailedBatch(chunk, chunkIdentifier);
					failedChunkKeys.add(failedChunkKey);
					log.error("Failed to process chunk {}, saved to {}", chunkNumber, failedChunkKey);
				}

				processedCount += chunk.size();
			}

			// 원본 실패 키 삭제 (성공/실패 여부와 관계없이 처리 완료로 간주)
			redisTemplate.delete(failedKey);

			// 결과 설정
			result.setStatus(failedChunks == 0 ? "ALL_SUCCESS" : "PARTIAL_SUCCESS");
			result.setTotalProcessed(processedCount);
			result.setSuccessfulChunks(successfulChunks);
			result.setFailedChunks(failedChunks);
			result.setFailedChunkKeys(failedChunkKeys);

			log.info("Failed batch {} retry completed. Processed: {}, Successful chunks: {}, Failed chunks: {}", 
				failedKey, processedCount, successfulChunks, failedChunks);

			return result;

		} catch (Exception e) {
			log.error("Error retrying failed batch {}", batchTime, e);
			result.setStatus("ERROR");
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}

	/**
	 * 실패한 배치에서 청크 단위로 데이터를 가져오고 Redis에서 제거
	 */
	private List<ShortUrlData> getChunkFromFailedBatch(String failedKey, int chunkSize) {
		List<ShortUrlData> chunk = new ArrayList<>();
		
		try {
			List<Object> results = redisTemplate.executePipelined(new RedisCallback<Object>() {
				@Override
				public Object doInRedis(RedisConnection connection) {
					for (int i = 0; i < chunkSize; i++) {
						connection.rPop(redisTemplate.getStringSerializer().serialize(failedKey));
					}
					return null;
				}
			});

			chunk = results.stream()
				.filter(obj -> obj != null)
				.map(obj -> {
					try {
						return objectMapper.convertValue(obj, ShortUrlData.class);
					} catch (Exception e) {
						log.error("Error converting ShortUrlData during chunk processing: {}", e.getMessage(), e);
						// 변환 실패한 데이터는 별도 저장을 위해 원시 데이터로 보관
						saveCorruptedData(obj, failedKey);
						return null;
					}
				})
				.filter(data -> data != null)
				.toList();

		} catch (Exception e) {
			log.error("Error fetching chunk from failed batch {}", failedKey, e);
		}

		return chunk;
	}

	/**
	 * JSON 변환 실패한 손상된 데이터를 별도 저장
	 */
	private void saveCorruptedData(Object corruptedData, String originalKey) {
		try {
			String corruptedKey = "corrupted:data:" + System.currentTimeMillis();
			redisTemplate.opsForValue().set(corruptedKey, corruptedData, Duration.ofDays(7));
			log.warn("Corrupted data saved to {} from {}", corruptedKey, originalKey);
		} catch (Exception e) {
			log.error("Failed to save corrupted data from {}", originalKey, e);
		}
	}

	/**
	 * 재시도 결과를 담는 내부 클래스
	 */
	public static class RetryResult {
		private String status;
		private int totalProcessed;
		private int successfulChunks;
		private int failedChunks;
		private List<String> failedChunkKeys = new ArrayList<>();
		private String errorMessage;

		// Getters and Setters
		public String getStatus() { return status; }
		public void setStatus(String status) { this.status = status; }
		
		public int getTotalProcessed() { return totalProcessed; }
		public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
		
		public int getSuccessfulChunks() { return successfulChunks; }
		public void setSuccessfulChunks(int successfulChunks) { this.successfulChunks = successfulChunks; }
		
		public int getFailedChunks() { return failedChunks; }
		public void setFailedChunks(int failedChunks) { this.failedChunks = failedChunks; }
		
		public List<String> getFailedChunkKeys() { return failedChunkKeys; }
		public void setFailedChunkKeys(List<String> failedChunkKeys) { this.failedChunkKeys = failedChunkKeys; }
		
		public String getErrorMessage() { return errorMessage; }
		public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

		@Override
		public String toString() {
			return String.format("RetryResult{status='%s', totalProcessed=%d, successfulChunks=%d, failedChunks=%d, failedChunkKeys=%s}", 
				status, totalProcessed, successfulChunks, failedChunks, failedChunkKeys);
		}
	}
}
