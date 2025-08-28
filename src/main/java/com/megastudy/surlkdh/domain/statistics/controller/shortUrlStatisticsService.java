package com.megastudy.surlkdh.domain.statistics.controller;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;
import com.megastudy.surlkdh.domain.statistics.infrastructure.ShortUrlStatisticsRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class shortUrlStatisticsService {
    private final ShortUrlStatisticsRepositoryImpl shortUrlStatisticsRepository;

    public boolean processBatch(List<ShortUrlData> batch) {
        log.info("Processing {} statistics records", batch.size());
        shortUrlStatisticsRepository.processBatch(batch);
        return true;
    }
}
