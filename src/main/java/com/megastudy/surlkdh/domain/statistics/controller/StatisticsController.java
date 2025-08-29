package com.megastudy.surlkdh.domain.statistics.controller;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "통계 API", description = "통계 관련 API")
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EMPLOYEE')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/")
    public ApiResponse<Page<StatisticsDataPoint>> getStatistics(
            StatisticRequest request,
            @PageableDefault(size = 10, sort = "clicks", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<StatisticsDataPoint> response = statisticsService.getGroupByStatistics(request, pageable);
        return ApiResponse.ok(response);
    }
}