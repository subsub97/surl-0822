package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrlRedirectRuleEntity {
	private Long redirectRuleId;
	private Long shortUrlId;
	private String deviceType;
	private String targetUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static ShortUrlRedirectRuleEntity from(ShortUrlRedirectRule rule) {
		return ShortUrlRedirectRuleEntity.builder()
			.shortUrlId(rule.getShortUrlId())
			.deviceType(rule.getDeviceType().name())
			.targetUrl(rule.getTargetUrl())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public ShortUrlRedirectRule toDomain() {
		return ShortUrlRedirectRule.builder()
			.redirectRuleId(redirectRuleId)
			.shortUrlId(shortUrlId)
			.deviceType(deviceType != null ? DeviceType.valueOf(deviceType) : null)
			.targetUrl(targetUrl)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}