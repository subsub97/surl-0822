package com.megastudy.surlkdh.domain.statistics.infrastructure;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticClicksRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.entity.ShortUrlClicks;
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

    public Page<StatisticsDataPoint> getGroupedStatisticsByShortUrl(Long shortUrlId, StatisticRequest request, Pageable pageable) {
        List<StatisticsDataPoint> content = shortUrlStatisticsMapper.findStatisticsByShortUrl(shortUrlId, request, pageable);
        long total = shortUrlStatisticsMapper.countStatisticsByShortUrl(shortUrlId, request);
        return new PageImpl<>(content, pageable, total);
    }

    public Page<ShortUrlClicks> getShortUrlClicks(Long shortUrlId, StatisticClicksRequest request, Pageable pageable) {
        List<ShortUrlClicks> content = shortUrlStatisticsMapper.findShortUrlClicksById(shortUrlId, request, pageable);
        long total = shortUrlStatisticsMapper.countShortUrlClicksById(shortUrlId, request);
        return new PageImpl<>(content, pageable, total);
    }

    public long getTotalClicks(Long shortUrlId, StatisticRequest request) {
        return shortUrlStatisticsMapper.sumTotalClicks(shortUrlId, request);
    }
}
