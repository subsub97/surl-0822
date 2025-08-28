package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.auth.entity.UserType;
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
	private String department;
	private Long creatorId;
	private String creatorType;
	private String note;
	private String pcUrl;
	private String mobileUrl;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static ShortUrlEntity from(ShortUrl shortUrl) {
		return ShortUrlEntity.builder()
			.shortUrlId(shortUrl.getShortUrlId())
			.shortCode(shortUrl.getShortCode())
			.department(shortUrl.getDepartment().name())
			.creatorId(shortUrl.getCreatorId())
			.creatorType(shortUrl.getCreatorType().getType())
			.note(shortUrl.getNote())
			.pcUrl(shortUrl.getPcUrl())
			.mobileUrl(shortUrl.getMobileUrl())
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
			.pcUrl(pcUrl)
			.mobileUrl(mobileUrl)
			.expiresAt(expiresAt)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}