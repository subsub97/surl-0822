package com.megastudy.surlkdh.domain.statistics.entity;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShorturlClicksSec extends BaseTimeEntity {

    private Long shortUrlId;
    private String countryCode;
    private String deviceType;
    private LocalDateTime tsSec;
    private String referrer;
    private int clicksCnt;

    @Builder
    public ShorturlClicksSec(Long shortUrlId, String countryCode, String deviceType, LocalDateTime tsSec, String referrer, int clicksCnt) {
        this.shortUrlId = shortUrlId;
        this.countryCode = countryCode;
        this.deviceType = deviceType;
        this.tsSec = tsSec;
        this.referrer = referrer;
        this.clicksCnt = clicksCnt;
    }
}