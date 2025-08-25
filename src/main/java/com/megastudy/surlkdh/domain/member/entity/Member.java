package com.megastudy.surlkdh.domain.member.entity;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {
	private Long memberId;
	private String email;
	private String password;
	private Department department;
	private Role role;

	public static Member from(String email, String password, Department department, Role role) {
		return Member.builder()
			.email(email)
			.password(password)
			.department(department)
			.role(role)
			.build();
	}

	public static Member from(Long memberId, String email, String password, Department department,
		Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return Member.builder()
			.memberId(memberId)
			.email(email)
			.password(password)
			.department(department)
			.role(role)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
