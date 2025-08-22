package com.megastudy.surlkdh.domain.shorturl.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.domain.member.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrl extends BaseTimeEntity {
	private Long shortUrlId;
	private String shortCode;
	private Department departmentName;
	private Long creatorId;
	private String creatorType; // JWT, API Token 등 생성자 타입
	private String note;
	List<ShortUrlRedirectRule> shortUrlRedirectRules;
	private LocalDateTime expiresAt;

	public static ShortUrl create(String shortCode, Department departmentName,
		Long creatorId, String creatorType, List<ShortUrlRedirectRule> shortUrlRedirectRules, LocalDateTime expiresAt,
		String note) {
		return ShortUrl.builder()
			.shortCode(shortCode)
			.departmentName(departmentName)
			.creatorId(creatorId)
			.creatorType(creatorType)
			.expiresAt(expiresAt)
			.note(note)
			.shortUrlRedirectRules(shortUrlRedirectRules)
			.build();
	}

	public boolean isExpired() {
		return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
	}

	public String getFullShortUrl() {
		//TODO host 도메인 미정으로 localhost 하드코딩
		return "localhost/" + shortCode;
	}
}

