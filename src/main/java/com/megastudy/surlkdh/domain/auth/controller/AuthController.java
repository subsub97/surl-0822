package com.megastudy.surlkdh.domain.auth.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.SignUpRequest;
import com.megastudy.surlkdh.domain.auth.controller.port.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth", description = "인증 및 회원 관리 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(
		summary = "회원가입",
		description = "새로운 사용자를 등록합니다. 이메일 중복 확인이 필요합니다. [MARKETING, FINANCE, DEVELOPMENT, SALES, ADMIN, HR] 부서 중 하나를 선택해야 합니다."
	)
	@PostMapping("/api/v1/auth/signup")
	public ApiResponse<Void> createMember(@Validated @RequestBody SignUpRequest signUpRequest) {
		authService.signUp(signUpRequest);
		return ApiResponse.created(null);
	}
}

