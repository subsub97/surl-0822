package com.megastudy.surlkdh.domain.statistics.controller.dto.request;

import com.megastudy.surlkdh.domain.statistics.controller.dto.GroupBy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StatisticRequest {
    private final GroupBy groupBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime endDate;
}