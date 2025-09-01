package com.megastudy.surlkdh.domain.statistics.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class GroupedStatisticsResponse {
    private final long totalClicks;
    private final Page<StatisticsDataPoint> groupedDataPage;
}
