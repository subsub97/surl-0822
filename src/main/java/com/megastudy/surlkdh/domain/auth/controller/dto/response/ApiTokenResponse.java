package com.megastudy.surlkdh.domain.auth.controller.dto.response;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiTokenResponse {
	private Long apiTokenId;
	private Long memberId;
	private String tokenName;
	private String tokenValue;
	private LocalDateTime expiresAt;
	private LocalDateTime lastUsedAt;
	private Role role;
	private Department department;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isExpired;

	public static ApiTokenResponse of(ApiToken apiToken, boolean includeTokenValue) {
		return new ApiTokenResponse(
			apiToken.getApiTokenId(),
			apiToken.getMemberId(),
			apiToken.getTokenName(),
			includeTokenValue ? apiToken.getTokenValue() : maskToken(apiToken.getTokenValue()),
			apiToken.getExpiresAt(),
			apiToken.getLastUsedAt(),
			apiToken.getRole(),
			apiToken.getDepartment(),
			apiToken.getCreatedAt(),
			apiToken.getUpdatedAt(),
			apiToken.isExpired()
		);
	}

	private static String maskToken(String tokenValue) {
		if (tokenValue == null || tokenValue.length() < 8) {
			return "****";
		}
		return tokenValue.substring(0, 4) + "****" + tokenValue.substring(tokenValue.length() - 4);
	}
}