package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.domain.member.entity.Department;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateShortUrlRequest {

	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,20}$", message = "URL 형식을 확인해주세요. 최소 1자리 이상 20자리 이하만 가능합니다.")
	private String shortCode;
	private String note;
	private LocalDateTime expiresAt;
	private Department department;

	private String pcUrl;
	private String mobileUrl;
}