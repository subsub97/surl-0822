package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShortUrlDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShortUrlStatisticsMapper {
    void bulkInsert(List<ShortUrlDataEntity> statsList);
}
