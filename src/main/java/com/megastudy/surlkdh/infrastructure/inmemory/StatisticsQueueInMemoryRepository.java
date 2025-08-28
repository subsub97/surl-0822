package com.megastudy.surlkdh.infrastructure.inmemory;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.service.port.StatisticsQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Repository
@Profile("local") // local 프로필에서만 활성화
public class StatisticsQueueInMemoryRepository implements StatisticsQueueRepository {

    private final Queue<ShortUrlData> queue = new ConcurrentLinkedQueue<>();
    private final ConcurrentMap<String, List<ShortUrlData>> failedBatchStore = new ConcurrentHashMap<>();

    @Override
    public void save(ShortUrlData data) {
        queue.offer(data);
    }

    @Override
    public List<ShortUrlData> getBatch(int batchSize) {
        List<ShortUrlData> batch = new ArrayList<>();
        for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
            batch.add(queue.poll());
        }
        return batch;
    }

    @Override
    public Long getQueueSize() {
        return (long) queue.size();
    }

    @Override
    public void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
        failedBatchStore.put(batchTime, new ArrayList<>(failedBatch));
        log.info("In-memory failed batch saved: {} with {} items", batchTime, failedBatch.size());
    }

    @Override
    public List<ShortUrlData> getFailedBatch(String batchTime) {
        return failedBatchStore.getOrDefault(batchTime, List.of());
    }

    @Override
    public void deleteFailedBatch(String batchTime) {
        failedBatchStore.remove(batchTime);
    }

    @Override
    public Long getFailedBatchSize(String batchTime) {
        return (long) failedBatchStore.getOrDefault(batchTime, List.of()).size();
    }
}
