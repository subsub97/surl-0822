package com.megastudy.surlkdh.domain.statistics.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticClicksRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.GroupedStatisticsResponse;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.ShortUrlClickResponse;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "통계 API", description = "통계 관련 API")
@RestController
@RequestMapping("/api/v1/statistics/urls/{shortUrlId}")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EMPLOYEE')")
public class StatisticsController {

	private final StatisticsService statisticsService;

	@Operation(
		summary = "단축 URL 그룹 통계 조회",
		description = "특정 단축 URL에 대한 통계를 그룹별로 조회합니다. groupBy 파라미터로 그룹 기준을 지정할 수 있습니다."
	)
	@GetMapping("/grouped")
	public ApiResponse<GroupedStatisticsResponse> getGroupedStatistics(
		@PathVariable Long shortUrlId,
		StatisticRequest request,
		@PageableDefault(size = 50, sort = "clicks", direction = Sort.Direction.DESC) Pageable pageable
	) {
		GroupedStatisticsResponse response = statisticsService.getGroupedStatistics(shortUrlId, request, pageable);
		return ApiResponse.ok(response);
	}

	@Operation(
		summary = "단축 URL 개별 클릭 내역 조회",
		description = "특정 단축 URL의 개별 클릭 내역을 시간순으로 조회합니다."
	)
	@GetMapping("/clicks")
	public ApiResponse<Page<ShortUrlClickResponse>> getShortUrlClicks(
		@PathVariable Long shortUrlId,
		StatisticClicksRequest request,
		@PageableDefault(size = 50, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<ShortUrlClickResponse> response = statisticsService.getShortUrlClicks(shortUrlId, request, pageable);
		return ApiResponse.ok(response);
	}
}
