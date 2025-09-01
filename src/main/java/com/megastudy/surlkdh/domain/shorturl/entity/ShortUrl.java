package com.megastudy.surlkdh.domain.shorturl.entity;

import java.time.LocalDateTime;
import java.util.function.Function;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.domain.shorturl.exception.ShortUrlErrorCode;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.domain.auth.entity.UserType;
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
	private Department department;
	private Long creatorId;
	private UserType creatorType;
	private String note;
	private String pcUrl;
	private String mobileUrl;
	private LocalDateTime expiresAt;

	public static ShortUrl create(String shortCode, Department department,
		Long creatorId, UserType creatorType, LocalDateTime expiresAt, String note, String pcUrl, String mobileUrl) {
		return ShortUrl.builder()
			.shortCode(shortCode)
			.department(department)
			.creatorId(creatorId)
			.creatorType(creatorType)
			.expiresAt(expiresAt)
			.note(note)
			.pcUrl(pcUrl)
			.mobileUrl(mobileUrl)
			.build();
	}

	public void changeShortCode(String newShortCode, Function<String, Boolean> isShortCodeAvailable) {

		if (newShortCode == null || newShortCode.isEmpty()) {
			throw new BusinessException(ShortUrlErrorCode.INVALID_URL);
		}
		if (newShortCode.equals(this.shortCode)) {
			return;
		}
		if (!isShortCodeAvailable.apply(newShortCode)) {
			throw new BusinessException(ShortUrlErrorCode.DUPLICATION_ERROR);
		}

		this.shortCode = newShortCode;
	}

	public void updateNote(String newNote) {
		this.note = newNote;
	}

	public void setExpiration(LocalDateTime newExpiresAt) {
		if (newExpiresAt != null && newExpiresAt.isBefore(LocalDateTime.now())) {
			throw new BusinessException(ShortUrlErrorCode.INVALID_URL);
		}
		this.expiresAt = newExpiresAt;
	}

	public void transferDepartment(Department newDepartment) {
		if (newDepartment == null) {
			throw new BusinessException(CommonErrorCode.BAD_REQUEST);
		}
		this.department = newDepartment;
	}

	public void changePcUrl(String newPcUrl) {
		if (newPcUrl == null || newPcUrl.isEmpty()) {
			throw new BusinessException(ShortUrlErrorCode.INVALID_URL);
		}
		this.pcUrl = newPcUrl;
	}

	public void changeMobileUrl(String mobileUrl) {
		if (mobileUrl == null || mobileUrl.isEmpty()) {
			throw new BusinessException(ShortUrlErrorCode.INVALID_URL);
		}
		this.pcUrl = mobileUrl;
	}

	public boolean isExpired() {
		return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
	}

}