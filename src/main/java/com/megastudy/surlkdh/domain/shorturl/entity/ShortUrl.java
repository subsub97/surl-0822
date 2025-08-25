package com.megastudy.surlkdh.domain.shorturl.entity;

import java.time.LocalDateTime;
import java.util.List;

import java.util.function.Function;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrl extends BaseTimeEntity {
	private Long shortUrlId;
	private String shortCode;
	private Department department;
	private Long creatorId;
	private UserType creatorType;
	private String note;
	List<ShortUrlRedirectRule> shortUrlRedirectRules;
	private LocalDateTime expiresAt;

	public static ShortUrl create(String shortCode, Department department,
		Long creatorId, UserType creatorType, LocalDateTime expiresAt,
		String note) {
		return ShortUrl.builder()
			.shortCode(shortCode)
			.department(department)
			.creatorId(creatorId)
			.creatorType(creatorType)
			.expiresAt(expiresAt)
			.note(note)
			.build();
	}

	// New update method
	public void update(String newShortCode, String newNote, LocalDateTime newExpiresAt, Department newDepartment,
					   Function<String, Boolean> isShortCodeAvailable) {
		// Update shortCode if provided and different
		if (newShortCode != null && !newShortCode.isEmpty() && !newShortCode.equals(this.shortCode)) {
			if (!isShortCodeAvailable.apply(newShortCode)) {
				throw new BusinessException(CommonErrorCode.DUPLICATION_ERROR); // Or a more specific error
			}
			this.shortCode = newShortCode;
		}

		// Update note if provided
		if (newNote != null) {
			this.note = newNote;
		}

		// Update expiresAt if provided
		if (newExpiresAt != null) {
			this.expiresAt = newExpiresAt;
		}

		// Update department if provided
		if (newDepartment != null) {
			this.department = newDepartment;
		}

		// Update updatedAt timestamp (assuming BaseTimeEntity handles this, or set here)
		// If BaseTimeEntity has setUpdatedAt, call it. Otherwise, you might need to add it here.
		// For now, assuming BaseTimeEntity handles it or it's set in repository.
		// If not, you might need to add setUpdatedAt(LocalDateTime.now());
	}


	public void changeShortCode(String shortCode) {
		if (shortCode == null || shortCode.isEmpty()) {
			throw new BusinessException(CommonErrorCode.SERVER_ERROR);
		}
		this.shortCode = shortCode;
	}

	public boolean isExpired() {
		return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
	}

	public String getFullShortUrl() {
		//TODO host 도메인 미정으로 localhost 하드코딩
		return "localhost/" + shortCode;
	}
}