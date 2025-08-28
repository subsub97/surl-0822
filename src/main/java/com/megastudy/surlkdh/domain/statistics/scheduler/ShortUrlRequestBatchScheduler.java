package com.megastudy.surlkdh.domain.statistics.scheduler;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.controller.shortUrlStatisticsService;
import com.megastudy.surlkdh.domain.statistics.service.port.StatisticsQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortUrlRequestBatchScheduler {

    private final StatisticsQueueRepository statisticsQueueRepository;
    private final shortUrlStatisticsService shortUrlStatisticsService;

    private static final int BATCH_SIZE = 1000;
    private static final long ONE_MIN = 1000 * 60L; // 1분

    @Scheduled(fixedRate = ONE_MIN)
    public void aggregateShortUrlRequests() {
        String batchTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        log.info("ShortUrl Request Batch Job started!! currentTime={}", batchTime);

        try {
            Long queueSize = statisticsQueueRepository.getQueueSize();

            if (queueSize == null || queueSize == 0) {
                log.info("No ShortUrl Request Data to process. Exiting batch job.");
                return;
            }

            log.info("Total ShortUrl Request Data to process: {}", queueSize);

            int processedCount = 0;
            while (true) {
                long currentBatchSize = Math.min(BATCH_SIZE, queueSize - processedCount);
                if (currentBatchSize <= 0) break;

                List<ShortUrlData> batch = statisticsQueueRepository.getBatch((int) currentBatchSize);

                if (batch.isEmpty()) break;

                boolean success = processBatchData(batch, batchTime);

                if (success) {
                    processedCount += batch.size();
                } else {
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
        }
    }

    private boolean processBatchData(List<ShortUrlData> batch, String batchTime) {
        try {
            if (batch == null || batch.isEmpty()) {
                return true; // 빈 배치는 성공으로 간주
            }

            boolean result = shortUrlStatisticsService.processBatch(batch);

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

    private void saveFailedBatch(List<ShortUrlData> failedBatch, String batchTime) {
        statisticsQueueRepository.saveFailedBatch(failedBatch, batchTime);
    }

    public void retryFailedBatch(String batchTime) {
        try {
            Long size = statisticsQueueRepository.getFailedBatchSize(batchTime);
            if (size == null || size == 0) {
                log.warn("Failed batch not found: {}", batchTime);
                return;
            }

            List<ShortUrlData> failedData = statisticsQueueRepository.getFailedBatch(batchTime);

            if (!failedData.isEmpty()) {
                boolean success = processBatchData(failedData, batchTime + "_retry");

                if (success) {
                    statisticsQueueRepository.deleteFailedBatch(batchTime);
                    log.info("Failed batch {} retried successfully and removed", batchTime);
                } else {
                    log.warn("Failed batch {} retry failed", batchTime);
                }
            }
        } catch (Exception e) {
            log.error("Error retrying failed batch {}", batchTime, e);
        }
    }
}
