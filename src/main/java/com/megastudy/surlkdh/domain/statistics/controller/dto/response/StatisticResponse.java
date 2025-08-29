package com.megastudy.surlkdh.domain.statistics.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StatisticResponse {
    private String key;
    private long clicks;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
