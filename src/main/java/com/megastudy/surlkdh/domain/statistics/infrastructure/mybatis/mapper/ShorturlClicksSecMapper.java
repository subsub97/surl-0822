package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShorturlClicksSecEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShorturlClicksSecMapper {

    void upsertAll(List<ShorturlClicksSecEntity> statistics);
}
