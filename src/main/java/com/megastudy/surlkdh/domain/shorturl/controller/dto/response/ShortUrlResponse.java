package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShortUrlResponse {

	private final Long shortUrlId;
	private final String shortCode;
	private final String department;
	private final Long creatorId;
	private final UserType creatorType;
	private final String note;
	private final String pcUrl;
	private final String mobileUrl;
	private final LocalDateTime expiresAt;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static ShortUrlResponse from(ShortUrl shortUrl) {
		return ShortUrlResponse.builder()
			.shortUrlId(shortUrl.getShortUrlId())
			.shortCode(shortUrl.getShortCode())
			.department(shortUrl.getDepartment().getName())
			.creatorId(shortUrl.getCreatorId())
			.creatorType(shortUrl.getCreatorType())
			.note(shortUrl.getNote())
			.pcUrl(shortUrl.getPcUrl())
			.mobileUrl(shortUrl.getMobileUrl())
			.expiresAt(shortUrl.getExpiresAt())
			.createdAt(shortUrl.getCreatedAt())
			.updatedAt(shortUrl.getUpdatedAt())
			.build();
	}
}
