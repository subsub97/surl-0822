package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;

import com.megastudy.surlkdh.domain.member.entity.Department;

import jakarta.validation.constraints.NotBlank;
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

	@URL(message = "타겟 원본 URL 형식이 올바르지 않습니다.")
	@NotBlank(message = "타겟 원본 URL은 필수입니다.")
	private String pcUrl;

	@URL(message = "타겟 원본 URL 형식이 올바르지 않습니다.")
	@NotBlank(message = "타겟 원본 URL은 필수입니다.")
	private String mobileUrl;

}