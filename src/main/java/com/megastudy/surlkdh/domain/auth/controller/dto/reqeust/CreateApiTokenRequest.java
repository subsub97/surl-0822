package com.megastudy.surlkdh.domain.auth.controller.dto.reqeust;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApiTokenRequest {

	@NotBlank(message = "토큰 이름은 필수입니다")
	@Size(max = 50, message = "토큰 이름은 최대 50자까지 입력 가능합니다")
	private String tokenName;

	private Role role;

	private Department department;

	private LocalDateTime expiresAt;
}
