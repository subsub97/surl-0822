package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto;

import com.megastudy.surlkdh.domain.statistics.controller.dto.request.ShortUrlData;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrlDataEntity {
    private Long shortUrlId;
    private String countryCode;
    private String deviceType;
    private String referrer;
    private String domain;
    private LocalDateTime timestamp;

    public static ShortUrlDataEntity from(ShortUrlData shortUrlData) {
        return ShortUrlDataEntity.builder()
                .shortUrlId(shortUrlData.getShortUrlId())
                .countryCode(shortUrlData.getCountryCode())
                .deviceType(shortUrlData.getDeviceType())
                .referrer(shortUrlData.getReferrer())
                .domain(shortUrlData.getDomain())
                .timestamp(shortUrlData.getTimestamp())
                .build();
    }
}
