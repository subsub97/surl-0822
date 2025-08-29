package com.megastudy.surlkdh.domain.auth.controller;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.audit.aop.AuditLog;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.controller.port.ApiTokenService;
import com.megastudy.surlkdh.domain.auth.service.dto.AuthenticatedMember;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API Token", description = "API 토큰 관리 API, 로그인 사용자만 사용 가능, api-token 유저 불가능 )")
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('LEADER')")
public class ApiTokenController {

    private final ApiTokenService apiTokenService;

    @AuditLog(action = "CREATE_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 생성",
            description = "새로운 API 토큰을 생성합니다. ADMIN은 모든 부서, LEADER는 본인 부서만 가능합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/api-tokens")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.department)")
    public ApiResponse<ApiTokenResponse> createApiToken(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody CreateApiTokenRequest request) {
        AuthenticatedMember member = AuthenticatedMember.from(principal);
        ApiTokenResponse response = apiTokenService.createApiToken(request, member);
        return ApiResponse.created(response);
    }

    @AuditLog(action = "UPDATE_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 수정",
            description = "기존 API 토큰을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/api-tokens/{apiTokenId}")
    public ApiResponse<ApiTokenResponse> updateApiToken(
            @PathVariable Long apiTokenId,
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody UpdateApiTokenRequest request) {
        AuthenticatedMember member = AuthenticatedMember.from(principal);
        ApiTokenResponse response = apiTokenService.updateApiToken(apiTokenId, request, member);
        return ApiResponse.ok(response);
    }

    @AuditLog(action = "DELETE_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 삭제",
            description = "기존 API 토큰을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/api-tokens/{apiTokenId}")
    public ApiResponse<Void> deleteApiToken(
            @PathVariable Long apiTokenId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        AuthenticatedMember member = AuthenticatedMember.from(principal);
        apiTokenService.deleteApiToken(apiTokenId, member);
        return ApiResponse.ok(null);
    }

    @AuditLog(action = "VIEW_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 세부 조회",
            description = "API 토큰 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/api-tokens/{apiTokenId}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ApiResponse<ApiTokenResponse> getApiToken(@PathVariable Long apiTokenId,
                                                     @AuthenticationPrincipal MemberPrincipal principal) {
        AuthenticatedMember member = AuthenticatedMember.from(principal);
        ApiTokenResponse response = apiTokenService.getApiToken(apiTokenId, member);
        return ApiResponse.ok(response);
    }

    @AuditLog(action = "VIEW_DEPT_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "부서별 API 토큰 목록 조회",
            description = "본인이 소속한 부서의 API 토큰 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/departments/{department}/api-tokens")
    @PreAuthorize(
            "hasAuthority('ADMIN') " +
                    "or ((hasAuthority('LEADER') or hasAuthority('EMPLOYEE')) " +
                    "and #department.equals(authentication.principal.department))"
    )
    public ApiResponse<Page<ApiTokenResponse>> getApiTokens(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable(name = "department") Department department,
            @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Department : {}", department);
        Page<ApiTokenResponse> responses = apiTokenService.getApiTokensByDepartment(department, pageable);
        return ApiResponse.ok(responses);
    }

    @AuditLog(action = "VIEW_DEPT_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "나의 API 토큰 목록 조회",
            description = "내가 생성한 API 토큰 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me/api-tokens")
    public ApiResponse<Page<ApiTokenResponse>> getMyApiTokens(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<ApiTokenResponse> responses = apiTokenService.getApiTokensByMember(principal.getId(), pageable);
        return ApiResponse.ok(responses);
    }

    @AuditLog(action = "VIEW_API_TOKENS", resourceType = "API_TOKEN")
    @Operation(
            summary = "전체 API 토큰 목록 조회",
            description = "전체 API 토큰 목록을 조회합니다. (ADMIN 전용)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/api-tokens")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Page<ApiTokenResponse>> getAllApiTokens(
            @PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ApiTokenResponse> responses = apiTokenService.getAllApiTokens(pageable);
        return ApiResponse.ok(responses);
    }

}
