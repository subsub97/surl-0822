package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
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
public class ShortUrlEntity {

	private Long shortUrlId;
	private String shortCode;
	private String department;
	private Long creatorId;
	private String creatorType;
	private String note;
	private List<ShortUrlRedirectRuleEntity> shortUrlRedirectRules;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static ShortUrlEntity from(ShortUrl shortUrl) {
		return ShortUrlEntity.builder()
			.shortCode(shortUrl.getShortCode())
			.department(shortUrl.getDepartment().name())
			.creatorId(shortUrl.getCreatorId())
			.creatorType(shortUrl.getCreatorType().getType())
			.note(shortUrl.getNote())
			.expiresAt(shortUrl.getExpiresAt())
			.createdAt(shortUrl.getCreatedAt())
			.updatedAt(shortUrl.getUpdatedAt())
			.deletedAt(shortUrl.getDeletedAt())
			.build();
	}

	public ShortUrl toDomain() {
		return ShortUrl.builder()
			.shortUrlId(shortUrlId)
			.shortCode(shortCode)
			.department(Department.fromName(department))
			.creatorId(creatorId)
			.creatorType(UserType.findByType(creatorType))
			.note(note)
			.shortUrlRedirectRules(shortUrlRedirectRules == null ? Collections.emptyList() :
				shortUrlRedirectRules.stream()
					.map(rule -> ShortUrlRedirectRule.from(rule.getTargetUrl(),
						DeviceType.fromUserAgent(rule.getDeviceType())))
					.collect(Collectors.toList()))
			.expiresAt(expiresAt)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}