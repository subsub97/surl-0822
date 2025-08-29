package com.megastudy.surlkdh.domain.statistics.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.domain.statistics.scheduler.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortUrlRequestBatchScheduler {

    private static final int BATCH_SIZE = 1000;
    private static final long ONE_MIN = 1000 * 60L; // 1분
    private static final String REDIS_STAT_KEY = "shortUrl:stat";
    private static final String FAILED_BATCH_PREFIX = "batch:jobs:failed:";
    private static final Duration FAILED_BATCH_TTL = Duration.ofDays(5); // 실패 배치 데이터 보관 기간

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final StatisticsService statisticsService;

    @Scheduled(fixedRate = ONE_MIN)
    public void aggregateShortUrlRequests() {
        String batchTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        log.info("ShortUrl Request Batch Job started!! currentTime={}", batchTime);

        try {
            Long queueSize = redisTemplate.opsForList().size(REDIS_STAT_KEY);

            if (queueSize == null || queueSize == 0) {
                log.info("No ShortUrl Request Data to process. Exiting batch job.");
                return;
            }

            log.info("Total ShortUrl Request Data to process: {}", queueSize);

            int processedCount = 0;
            while (true) {
                List<ShortUrlData> batch = getBatch(queueSize);

                if (batch.isEmpty()) break;

                boolean success = processBatchData(batch, batchTime);

                if (success) {
                    processedCount += batch.size();
                } else {
                    // 배치 처리 실패 시 해당 배치를 별도 키에 저장
                    saveFailedBatch(batch, batchTime);
                    log.warn("Batch processing failed. Stopping further processing. Size: {}", batch.size());
                    break;
                }
            }

            if (processedCount > 0) {
                log.info("Total processed ShortUrl Request Data: {}", processedCount);
            }
        } catch (Exception e) {
            log.error("Error in batch Processing: {}", e.getMessage(), e);
            // TODO email 또는 slack으로 알리기
        }

    }

    private List<ShortUrlData> getBatch(long listSize) {
        List<ShortUrlData> batch = new ArrayList<>();
        long currentBatchSize = Math.min(BATCH_SIZE, listSize);

        try {
            List<Object> results = redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) {
                    for (int i = 0; i < currentBatchSize; i++) {
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

    // 배치 데이터 처리
    private boolean processBatchData(List<ShortUrlData> batch, String batchTime) {
        try {
            if (batch == null || batch.isEmpty()) {
                return true; // 빈 배치는 성공으로 간주
            }

            boolean result = statisticsService.processBatch(batch);

            if (result) {
                log.debug("Batch {} processed successfully with {} items", batchTime, batch.size());
            } else {
                log.warn("Batch {} processing failed. Storing failed batch data.", batchTime);
            }
            return result;
        } catch (Exception e) {
            log.error("Error processing batch {}: {}", batchTime, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 실패한 배치를 별도 키에 저장
     */
    private void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
        try {
            String failedKey = FAILED_BATCH_PREFIX + batchTime;

            // 실패한 데이터를 새로운 리스트에 저장
            redisTemplate.opsForList().leftPushAll(failedKey, failedBatch.toArray());

            // TTL 설정 (5일)
            redisTemplate.expire(failedKey, FAILED_BATCH_TTL);

            log.info("Failed batch saved: {} with {} items, TTL: {} days",
                    failedKey, failedBatch.size(), FAILED_BATCH_TTL.toDays());

        } catch (Exception e) {
            log.error("Error saving failed batch {}", batchTime, e);
        }
    }

    /**
     * 실패한 배치 재처리
     * 수동 실행 예정
     */
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

                boolean success = processBatchData(failedData, batchTime + "_retry");

                if (success) {
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
