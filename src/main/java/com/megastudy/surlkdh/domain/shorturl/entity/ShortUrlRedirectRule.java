package com.megastudy.surlkdh.domain.shorturl.entity;

import com.megastudy.surlkdh.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlRedirectRule extends BaseTimeEntity {
	private Long redirectRuleId;
	private Long shortUrlId;
	private DeviceType deviceType;
	private String targetUrl;

	public static ShortUrlRedirectRule create(Long shortUrlId, DeviceType deviceType, String targetUrl) {
		return ShortUrlRedirectRule.builder()
			.shortUrlId(shortUrlId)
			.deviceType(deviceType)
			.targetUrl(targetUrl)
			.build();
	}
}