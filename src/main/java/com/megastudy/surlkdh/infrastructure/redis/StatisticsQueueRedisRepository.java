package com.megastudy.surlkdh.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.service.port.StatisticsQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class StatisticsQueueRedisRepository implements StatisticsQueueRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_STAT_KEY = "shortUrl:stat";
    private static final String FAILED_BATCH_PREFIX = "batch:jobs:failed:";
    private static final Duration FAILED_BATCH_TTL = Duration.ofDays(5);

    @Override
    public void save(ShortUrlData data) {
        try {
            redisTemplate.opsForList().leftPush(REDIS_STAT_KEY, data);
        } catch (Exception e) {
            log.error("Failed to save ShortUrlData to Redis queue", e);
            // 예외를 던지거나 다른 처리를 할 수 있습니다.
        }
    }

    @Override
    public List<ShortUrlData> getBatch(int batchSize) {
        try {
            List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (int i = 0; i < batchSize; i++) {
                    connection.lPop(redisTemplate.getStringSerializer().serialize(REDIS_STAT_KEY));
                }
                return null;
            });

            return results.stream()
                    .filter(obj -> obj != null)
                    .map(obj -> objectMapper.convertValue(obj, ShortUrlData.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching batch from Redis", e);
            return List.of();
        }
    }

    @Override
    public Long getQueueSize() {
        try {
            return redisTemplate.opsForList().size(REDIS_STAT_KEY);
        } catch (Exception e) {
            log.error("Error getting queue size from Redis", e);
            return 0L;
        }
    }

    @Override
    public void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
        if (failedBatch == null || failedBatch.isEmpty()) {
            return;
        }
        String failedKey = FAILED_BATCH_PREFIX + batchTime;
        try {
            redisTemplate.opsForList().leftPushAll(failedKey, failedBatch.toArray());
            redisTemplate.expire(failedKey, FAILED_BATCH_TTL);
            log.info("Failed batch saved: {} with {} items, TTL: {} days",
                    failedKey, failedBatch.size(), FAILED_BATCH_TTL.toDays());
        } catch (Exception e) {
            log.error("Error saving failed batch {}", batchTime, e);
        }
    }

    @Override
    public List<ShortUrlData> getFailedBatch(String batchTime) {
        String failedKey = FAILED_BATCH_PREFIX + batchTime;
        try {
            List<Object> failedJsonData = redisTemplate.opsForList().range(failedKey, 0, -1);
            if (failedJsonData == null) {
                return List.of();
            }
            return failedJsonData.stream()
                    .map(json -> objectMapper.convertValue(json, ShortUrlData.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting failed batch from Redis", e);
            return List.of();
        }
    }

    @Override
    public void deleteFailedBatch(String batchTime) {
        String failedKey = FAILED_BATCH_PREFIX + batchTime;
        try {
            redisTemplate.delete(failedKey);
        } catch (Exception e) {
            log.error("Error deleting failed batch from Redis", e);
        }
    }

    @Override
    public Long getFailedBatchSize(String batchTime) {
        String failedKey = FAILED_BATCH_PREFIX + batchTime;
        try {
            return redisTemplate.opsForList().size(failedKey);
        } catch (Exception e) {
            log.error("Error getting failed batch size from Redis", e);
            return 0L;
        }
    }
}
