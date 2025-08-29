package com.megastudy.surlkdh.domain.statistics.entity;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShorturlClicksSec extends BaseTimeEntity {
    private Long shortUrlId;
    private String countryCode;
    private DeviceType deviceType;
    private LocalDateTime tsSec;
    private String referrer;

    public static ShorturlClicksSec create(Long shortUrlId, String countryCode, DeviceType deviceType,
                                           LocalDateTime tsSec, String referrer) {
        return ShorturlClicksSec.builder()
                .shortUrlId(shortUrlId)
                .countryCode(countryCode)
                .deviceType(deviceType)
                .tsSec(tsSec)
                .referrer(referrer)
                .build();
    }
}
