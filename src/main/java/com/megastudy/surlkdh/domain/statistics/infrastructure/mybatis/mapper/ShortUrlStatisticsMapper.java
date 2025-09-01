package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticClicksRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.entity.ShortUrlClicks;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShortUrlDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface ShortUrlStatisticsMapper {

    void bulkInsert(List<ShortUrlDataEntity> entityList);

    List<StatisticsDataPoint> findStatisticsByShortUrl(@Param("shortUrlId") Long shortUrlId,
                                                       @Param("request") StatisticRequest request,
                                                       @Param("pageable") Pageable pageable);

    long countStatisticsByShortUrl(@Param("shortUrlId") Long shortUrlId,
                                     @Param("request") StatisticRequest request);

    List<ShortUrlClicks> findShortUrlClicksById(@Param("shortUrlId") Long shortUrlId,
                                                @Param("request") StatisticClicksRequest request,
                                                @Param("pageable") Pageable pageable);

    long countShortUrlClicksById(@Param("shortUrlId") Long shortUrlId,
                                   @Param("request") StatisticClicksRequest request);

    long sumTotalClicks(@Param("shortUrlId") Long shortUrlId,
                        @Param("request") StatisticRequest request);
}
