package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;

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
public class ApiTokenEntity {
	private Long apiTokenId;
	private Long memberId;
	private String tokenName;
	private String tokenValue;
	private String role;
	private String departmentName;
	private LocalDateTime expiresAt;
	private LocalDateTime lastUsedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	/**
	 * Domain Entity에서 MyBatis Entity로 변환
	 */
	public static ApiTokenEntity from(ApiToken apiToken) {
		return ApiTokenEntity.builder()
			.apiTokenId(apiToken.getApiTokenId())
			.memberId(apiToken.getMemberId())
			.tokenName(apiToken.getTokenName())
			.tokenValue(apiToken.getTokenValue())
			.role(apiToken.getRole().getKey())
			.departmentName(apiToken.getDepartmentName().name())
			.expiresAt(apiToken.getExpiresAt())
			.lastUsedAt(apiToken.getLastUsedAt())
			.createdAt(apiToken.getCreatedAt())
			.updatedAt(apiToken.getUpdatedAt())
			.deletedAt(apiToken.getDeletedAt())
			.build();
	}

	/**
	 * MyBatis Entity에서 Domain Entity로 변환
	 */
	public ApiToken toModel() {
		return ApiToken.builder()
			.apiTokenId(apiTokenId)
			.memberId(memberId)
			.tokenName(tokenName)
			.tokenValue(tokenValue)
			.role(Role.fromKey(role))
			.departmentName(
				Department.fromName(departmentName))
			.expiresAt(expiresAt)
			.lastUsedAt(lastUsedAt)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.deletedAt(deletedAt)
			.build();
	}
}