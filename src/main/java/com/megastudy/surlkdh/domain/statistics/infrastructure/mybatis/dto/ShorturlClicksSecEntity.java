package com.megastudy.surlkdh.domain.statistics.infrastructure.mybatis.dto;

import com.megastudy.surlkdh.domain.statistics.entity.ShorturlClicksSec;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ShorturlClicksSecEntity {

    private Long shortUrlId;
    private String countryCode;
    private String deviceType;
    private LocalDateTime tsSec;
    private String referrer;
    private int clicksCnt;

    public static ShorturlClicksSecEntity from(ShorturlClicksSec domain) {
        ShorturlClicksSecEntity entity = new ShorturlClicksSecEntity();
        entity.shortUrlId = domain.getShortUrlId();
        entity.countryCode = domain.getCountryCode();
        entity.deviceType = domain.getDeviceType();
        entity.tsSec = domain.getTsSec();
        entity.referrer = domain.getReferrer();
        entity.clicksCnt = domain.getClicksCnt();
        return entity;
    }
}
