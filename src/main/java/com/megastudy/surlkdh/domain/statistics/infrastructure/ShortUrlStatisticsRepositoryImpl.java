package com.megastudy.surlkdh.domain.statistics.infrastructure;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShortUrlDataEntity;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper.ShortUrlStatisticsMapper;
import com.megastudy.surlkdh.domain.statistics.scheduler.dto.request.ShortUrlData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortUrlStatisticsRepositoryImpl {
    private final ShortUrlStatisticsMapper shortUrlStatisticsMapper;

    public void processBatch(List<ShortUrlData> batch) {
        List<ShortUrlDataEntity> entityList = batch.stream()
                .map(ShortUrlDataEntity::from)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        log.info("Inserting {} statistics records into DB Time : {}", entityList.size(), now);

        shortUrlStatisticsMapper.bulkInsert(entityList);

        log.info("Insertion complete started at: {}", now);
    }

    public Page<StatisticsDataPoint> getGroupByStatistics(StatisticRequest request, Pageable pageable) {
        List<StatisticsDataPoint> content = shortUrlStatisticsMapper.findGroupByStatistics(request, pageable);
        long total = shortUrlStatisticsMapper.countGroupByStatistics(request);
        return new PageImpl<>(content, pageable, total);
    }
}