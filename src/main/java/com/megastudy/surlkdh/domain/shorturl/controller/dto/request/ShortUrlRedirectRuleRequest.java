package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import org.hibernate.validator.constraints.URL;

import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlRedirectRuleRequest {

	private DeviceType deviceType;

	@URL(message = "타겟 원본 URL 형식이 올바르지 않습니다.")
	@NotBlank(message = "타겟 원본 URL은 필수입니다.")
	private String targetUrl;
}