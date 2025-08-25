package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
	private final List<ShortUrlRedirectRuleResponse> redirectRules; // 리디렉션 규칙 DTO 리스트
	private final LocalDateTime expiresAt;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static ShortUrlResponse from(ShortUrl shortUrl) {
		return ShortUrlResponse.builder()
			.shortUrlId(shortUrl.getShortUrlId())
			.shortCode(shortUrl.getShortCode())
			.department(shortUrl.getDepartment().getName()) // Department 객체에서 이름만 추출
			.creatorId(shortUrl.getCreatorId())
			.creatorType(shortUrl.getCreatorType())
			.note(shortUrl.getNote())
			.redirectRules(shortUrl.getShortUrlRedirectRules().stream()
				.map(ShortUrlRedirectRuleResponse::from) // 규칙 리스트도 DTO로 변환
				.collect(Collectors.toList()))
			.expiresAt(shortUrl.getExpiresAt())
			.createdAt(shortUrl.getCreatedAt())
			.updatedAt(shortUrl.getUpdatedAt())
			.build();
	}
}
