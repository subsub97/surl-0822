package com.megastudy.surlkdh.domain.member.infrastructure.mybatis.dto;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Member;
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
public class MemberEntity {
	private Long memberId;
	private String email;
	private String password;
	private String departmentName; // MyBatis mapping to String
	private String role; // MyBatis mapping to String
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static MemberEntity from(Member member) {
		return MemberEntity.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.password(member.getPassword())
			.departmentName(member.getDepartmentName().getName()) // Convert Department enum to String description
			.role(member.getRole().getKey()) // Convert Role enum to String key
			.createdAt(member.getCreatedAt())
			.updatedAt(member.getUpdatedAt())
			.deletedAt(member.getDeletedAt())
			.build();
	}

	public Member toModel() {
		return Member.builder()
			.memberId(memberId)
			.email(email)
			.password(password)
			.departmentName(Department.fromName(departmentName)) // Convert String description to Department enum
			.role(Role.fromKey(role)) // Convert String key to Role enum
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
