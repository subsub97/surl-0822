package com.megastudy.surlkdh.domain.statistics.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDataPoint {
    private String key;
    private long clicks;
}
