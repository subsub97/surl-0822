package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis;

import com.megastudy.surlkdh.domain.statistics.entity.ShorturlClicksSec;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto.ShorturlClicksSecEntity;
import com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.mapper.ShorturlClicksSecMapper;
import com.megastudy.surlkdh.domain.statistics.service.port.ShorturlClicksSecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ShorturlClicksSecRepositoryImpl implements ShorturlClicksSecRepository {

    private final ShorturlClicksSecMapper statisticsMapper;

    @Override
    public void upsertAll(List<ShorturlClicksSec> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return;
        }
        List<ShorturlClicksSecEntity> entities = statistics.stream()
                .map(ShorturlClicksSecEntity::from)
                .collect(Collectors.toList());
        statisticsMapper.upsertAll(entities);
    }
}
