package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShortUrlRedirectRuleResponse {

	private final DeviceType deviceType;
	private final String originalUrl;

	public static ShortUrlRedirectRuleResponse from(ShortUrlRedirectRule rule) {
		return ShortUrlRedirectRuleResponse.builder()
			.deviceType(rule.getDeviceType())
			.originalUrl(rule.getTargetUrl())
			.build();
	}
}

