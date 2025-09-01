package com.megastudy.surlkdh.domain.statistics.controller.dto.response;

import com.megastudy.surlkdh.domain.statistics.entity.ShortUrlClicks;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ShortUrlClickResponse {
    private final Long shortUrlId;
    private final String countryCode;
    private final String deviceType;
    private final String referrer;
    private final String domain;
    private final LocalDateTime createdAt;

    public static ShortUrlClickResponse from(ShortUrlClicks entity) {
        return ShortUrlClickResponse.builder()
                .shortUrlId(entity.getShortUrlId())
                .countryCode(entity.getCountryCode())
                .deviceType(entity.getDeviceType())
                .referrer(entity.getReferrer())
                .domain(entity.getDomain())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}