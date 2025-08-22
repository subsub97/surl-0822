package com.megastudy.surlkdh.domain.shorturl.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
	PC("PC", "데스크톱"),
	MOBILE("MOBILE", "모바일"),
	TABLET("TABLET", "태블릿");

	private final String key;
	private final String description;

	public static DeviceType fromUserAgent(String userAgent) {
		if (userAgent == null) {
			return PC;
		}

		String lowerUserAgent = userAgent.toLowerCase();

		if (lowerUserAgent.contains("mobile") ||
			lowerUserAgent.contains("android") ||
			lowerUserAgent.contains("iphone")) {
			return MOBILE;
		}

		if (lowerUserAgent.contains("tablet") ||
			lowerUserAgent.contains("ipad")) {
			return TABLET;
		}

		return PC;
	}
}
