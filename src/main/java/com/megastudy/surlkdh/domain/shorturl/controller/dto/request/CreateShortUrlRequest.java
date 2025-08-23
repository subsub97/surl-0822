package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.megastudy.surlkdh.domain.member.entity.Department;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrlRequest {

	@Size(max = 20, message = "단축 URL 코드는 최대 20자까지 가능합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "단축 URL 코드는 영문자와 숫자만 가능합니다.")
	private String shortCode;

	private Department department;

	private String note;

	private LocalDateTime expiresAt;

	@Valid
	private List<ShortUrlRedirectRuleRequest> redirectRules;
}