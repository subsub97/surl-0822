package com.megastudy.surlkdh.domain.statistics.entity;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
	private Integer clicksCnt;

	public static ShorturlClicksSec create(Long shortUrlId, String countryCode, DeviceType deviceType,
		LocalDateTime tsSec, String referrer, Integer clicksCnt) {
		return ShorturlClicksSec.builder()
			.shortUrlId(shortUrlId)
			.countryCode(countryCode)
			.deviceType(deviceType)
			.tsSec(tsSec)
			.referrer(referrer)
			.clicksCnt(clicksCnt)
			.build();
	}

	public void incrementClicks(int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Clicks count cannot be negative");
		}
		this.clicksCnt += count;
	}
}
