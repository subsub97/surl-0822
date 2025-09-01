package com.megastudy.surlkdh.domain.statistics.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlClicks {
	private Long shortUrlId;
	private String countryCode;
	private String deviceType;
	private LocalDateTime timestamp;
	private String domain;
	private String referrer;
	private LocalDateTime createdAt;

	public static ShortUrlClicks create(Long shortUrlId, String countryCode, String deviceType,
		LocalDateTime timestamp, String referrer) {
		return ShortUrlClicks.builder()
			.shortUrlId(shortUrlId)
			.countryCode(countryCode)
			.deviceType(deviceType)
			.timestamp(timestamp)
			.referrer(referrer)
			.build();
	}
}
