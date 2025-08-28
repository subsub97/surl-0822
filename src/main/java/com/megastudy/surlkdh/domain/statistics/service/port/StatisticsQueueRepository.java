package com.megastudy.surlkdh.domain.statistics.service.port;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;

import java.util.List;

public interface StatisticsQueueRepository {

    void save(ShortUrlData data);

    List<ShortUrlData> getBatch(int batchSize);

    Long getQueueSize();

    void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime);

    List<ShortUrlData> getFailedBatch(String batchTime);

    void deleteFailedBatch(String batchTime);

    Long getFailedBatchSize(String batchTime);
}
