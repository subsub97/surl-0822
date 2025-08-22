package com.megastudy.surlkdh.domain.shorturl.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Short URL", description = "단축 URL 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/short-urls")
@RequiredArgsConstructor
public class ShortUrlController {

	@Operation(
		summary = "단축 URL 생성",
		description = "새로운 단축 URL을 생성합니다. URL_CRUD 권한을 소유하고 본인이 소속된 부서만 가능합니다.",
		security = @SecurityRequirement(name = "api-token")
	)
	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.departmentName)")
	public ApiResponse<?> createShotUrl(
		@AuthenticationPrincipal MemberPrincipal principal,
		@Valid @RequestBody CreateShortUrlRequest request) {

		log.info("Creating short URL for request: {}", request);
		return null;
	}
}
