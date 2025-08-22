package com.megastudy.surlkdh.domain.auth.entity;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiToken extends BaseTimeEntity {
	private Long apiTokenId;
	private Long memberId;
	private String tokenName;
	private String tokenValue;
	private Role role;
	private Department departmentName;
	private LocalDateTime expiresAt;
	private LocalDateTime lastUsedAt;

	public static ApiToken create(Long memberId, String tokenName, String tokenValue,
		LocalDateTime expiresAt, Role role, Department departmentName) {

		return ApiToken.builder()
			.memberId(memberId)
			.tokenName(tokenName)
			.tokenValue(tokenValue)
			.expiresAt(expiresAt)
			.role(role)
			.departmentName(departmentName)
			.build();
	}

	public boolean isExpired() {
		return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
	}
}
