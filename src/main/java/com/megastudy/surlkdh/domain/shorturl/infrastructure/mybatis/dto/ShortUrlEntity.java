package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;

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
	private String departmentName;
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
			.departmentName(shortUrl.getDepartmentName().name())
			.creatorId(shortUrl.getCreatorId())
			.creatorType(shortUrl.getCreatorType())
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
			.departmentName(Department.fromName(departmentName))
			.creatorId(creatorId)
			.creatorType(creatorType)
			.note(note)
			.expiresAt(expiresAt)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}