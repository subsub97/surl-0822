package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShortUrlDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface ShortUrlStatisticsMapper {

    void bulkInsert(List<ShortUrlDataEntity> entityList);

    List<StatisticsDataPoint> findGroupByStatistics(@Param("request") StatisticRequest request, @Param("pageable") Pageable pageable);

    long countGroupByStatistics(@Param("request") StatisticRequest request);
}