package com.megastudy.surlkdh.domain.auth.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.controller.port.ApiTokenService;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "API Token", description = "API 토큰 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/api-tokens")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('LEADER')")
public class ApiTokenController {

	private final ApiTokenService apiTokenService;

	@Operation(
		summary = "API 토큰 생성",
		description = "새로운 API 토큰을 생성합니다. ADMIN은 모든 부서, LEADER는 본인 부서만 가능합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.department)")
	public ApiResponse<ApiTokenResponse> createApiToken(
		@AuthenticationPrincipal MemberPrincipal principal,
		@Valid @RequestBody CreateApiTokenRequest request) {

		ApiTokenResponse response = apiTokenService.createApiToken(request, principal.getId());
		return ApiResponse.created(response);
	}

	@Operation(
		summary = "API 토큰 수정",
		description = "기존 API 토큰을 수정합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@PutMapping("/{apiTokenId}")
	public ApiResponse<ApiTokenResponse> updateApiToken(
		@PathVariable Long apiTokenId,
		@AuthenticationPrincipal MemberPrincipal principal,
		@Valid @RequestBody UpdateApiTokenRequest request) {

		ApiTokenResponse response = apiTokenService.updateApiToken(apiTokenId, request, principal.getId());
		return ApiResponse.ok(response);
	}

	@Operation(
		summary = "API 토큰 삭제",
		description = "기존 API 토큰을 삭제합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@DeleteMapping("/{apiTokenId}")
	public ApiResponse<Void> deleteApiToken(
		@PathVariable Long apiTokenId,
		@AuthenticationPrincipal MemberPrincipal principal
	) {
		apiTokenService.deleteApiToken(apiTokenId, principal.getId());
		return ApiResponse.ok(null);
	}

	@Operation(
		summary = "API 토큰 세부 조회",
		description = "API 토큰 상세 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/{apiTokenId}")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ApiResponse<ApiTokenResponse> getApiToken(@PathVariable Long apiTokenId,
		@AuthenticationPrincipal MemberPrincipal principal) {
		ApiTokenResponse response = apiTokenService.getApiToken(apiTokenId, principal.getId());
		return ApiResponse.ok(response);
	}

	@Operation(
		summary = "내 API 토큰 목록 조회",
		description = "본인이 생성한 API 토큰 목록을 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping("/my")
	public ApiResponse<List<ApiTokenResponse>> getMyApiTokens(
		@AuthenticationPrincipal MemberPrincipal principal) {

		List<ApiTokenResponse> responses = apiTokenService.getApiTokensByMember(principal.getId());
		return ApiResponse.ok(responses);
	}

	@Operation(
		summary = "전체 API 토큰 목록 조회",
		description = "전체 API 토큰 목록을 조회합니다. (ADMIN 전용)",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN')")
	public ApiResponse<List<ApiTokenResponse>> getAllApiTokens() {
		List<ApiTokenResponse> responses = apiTokenService.getAllApiTokens();
		return ApiResponse.ok(responses);
	}

}
