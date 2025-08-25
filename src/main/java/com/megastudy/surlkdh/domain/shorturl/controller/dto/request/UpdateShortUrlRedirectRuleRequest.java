package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateShortUrlRedirectRuleRequest {

    private Long redirectRuleId;
    private DeviceType deviceType;
    private String targetUrl;
}