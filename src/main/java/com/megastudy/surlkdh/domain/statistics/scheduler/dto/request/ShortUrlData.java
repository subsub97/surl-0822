package com.megastudy.surlkdh.domain.statistics.scheduler.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ShortUrlData {
    private Long shortUrlId;
    private String countryCode;
    private String deviceType;
    private String referrer;
    private String domain;
    private LocalDateTime timestamp;
}
