package com.megastudy.surlkdh.domain.shorturl.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.domain.audit.aop.AuditLog;
import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.UpdateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortCodeResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.port.ShortUrlService;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Short URL", description = "단축 URL 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class ShortUrlController {

	private final ShortUrlService shortUrlService;

	@Operation(
		summary = "단축 URL 생성",
		description = "새로운 단축 URL을 생성합니다. URL_CRUD 권한을 소유하고 본인이 소속된 부서만 가능합니다."
	)
	@PostMapping
	@AuditLog(action = "CREATE_SHORT_URL", resourceType = "SHORT_URL")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.department)")
	public ResponseEntity<ApiResponse<ShortCodeResponse>> createShotUrl(
		@AuthenticationPrincipal MemberPrincipal principal,
		@Valid @RequestBody CreateShortUrlRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(
				shortUrlService.createShortUrl(request, principal.getId(),
					UserType.findByType(principal.getUsername()))));
	}

	@Operation(
		summary = "단축 URL 수정",
		description = "기존 생성된 단축 URL을 수정합니다. ADMIN 모두 가능, LEADER의 경우 본인이 소속된 부서만 변경 가능"
	)
	@PutMapping("/{shortUrlId}")
	@AuditLog(action = "UPDATE_SHORT_URL", resourceType = "SHORT_URL")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.department)")
	public ApiResponse<ShortUrlResponse> updateShortUrl(
		@AuthenticationPrincipal MemberPrincipal principal,
		@Valid @RequestBody UpdateShortUrlRequest request,
		@PathVariable Long shortUrlId
	) {
		ShortUrlResponse response = shortUrlService.updateShortUrl(shortUrlId, request, principal.getId(),
			UserType.findByType(principal.getUsername()));
		return ApiResponse.ok(response);
	}

	@Operation(
		summary = "단축 URL 단건 조회",
		description = "단축 URL을 세부 조회합니다. ADMIN은 전체 조회, LEADER,EMPLOYEE는 소속 부서의 URL만 조회 가능합니다."
	)
	@GetMapping("/{shortUrlId}")
	@AuditLog(action = "VIEW_SHORT_URL", resourceType = "SHORT_URL")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ApiResponse<ShortUrlResponse> getShotUrl(@AuthenticationPrincipal MemberPrincipal principal,
		@PathVariable Long shortUrlId) {
		log.info("UserType : {} , UserId : {} 의 사용자가 단축 URL ID {} 세부조회 요청", principal.getUsername(), principal.getId(),
			shortUrlId);

		return ApiResponse.ok(shortUrlService.getShortUrlByShortUrlId(shortUrlId,
			extractRole(principal.getAuthorities()), principal.getDepartment()));
	}

	@Operation(
		summary = "단축 URL 목록 조회",
		description = "단축 URL 목록을 조회합니다. ADMIN은 전체 조회, LEADER,EMPLOYEE는 소속 부서의 URL만 조회 가능합니다."
	)
	@GetMapping
	@AuditLog(action = "VIEW_SHORT_URLS", resourceType = "SHORT_URL")
	@PreAuthorize(
		"hasAuthority('ADMIN') " +
			"or ((hasAuthority('LEADER') or hasAuthority('EMPLOYEE')) " +
			"and #department.equals(authentication.principal.department.getName()))"
	)
	public ApiResponse<Page<ShortUrlResponse>> getShortUrls(
		@AuthenticationPrincipal MemberPrincipal principal,
		@RequestParam(name = "creator", required = false) String creator,
		@RequestParam(name = "department") String department,
		@PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<ShortUrlResponse> responses = shortUrlService.getShortUrls(extractRole(principal.getAuthorities()),
			Department.fromName(department), pageable);
		return ApiResponse.ok(responses);
	}

	@Operation(
		summary = "단축 URL 삭제",
		description = "단축 URL을 삭제합니다. ADMIN은 전체 삭제, LEADER는 소속 부서의 URL만 삭제 가능합니다."
	)
	@DeleteMapping("/{shortUrlId}")
	@AuditLog(action = "DELETE_SHORT_URL", resourceType = "SHORT_URL")
	@PreAuthorize("hasAuthority('LEADER')")
	public ApiResponse<Void> deleteShortUrl(@AuthenticationPrincipal MemberPrincipal principal,
		@PathVariable Long shortUrlId) {

		shortUrlService.deleteShortUrlByShortUrlId(shortUrlId, principal.getDepartment());

		return ApiResponse.ok(null);
	}

	private Role extractRole(Collection<? extends GrantedAuthority> authorities) {
		List<String> authorityNames = authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		if (authorityNames.contains("ADMIN")) {
			return Role.ADMIN;
		}

		if (authorityNames.contains("LEADER")) {
			return Role.LEADER;
		}

		if (authorityNames.contains("EMPLOYEE")) {
			return Role.EMPLOYEE;
		}
		throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
	}
}