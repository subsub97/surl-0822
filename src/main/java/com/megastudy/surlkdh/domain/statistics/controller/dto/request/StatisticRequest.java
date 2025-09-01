package com.megastudy.surlkdh.domain.statistics.controller.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.megastudy.surlkdh.domain.statistics.controller.dto.GroupBy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatisticRequest {
	private final GroupBy groupBy;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private final LocalDateTime startDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private final LocalDateTime endDate;
}