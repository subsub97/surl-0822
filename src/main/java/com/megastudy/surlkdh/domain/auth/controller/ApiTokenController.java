package com.megastudy.surlkdh.domain.auth.controller;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.audit.aop.AuditLog;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.controller.port.ApiTokenService;
import com.megastudy.surlkdh.domain.auth.service.dto.AuthenticatedUser;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "API Token", description = "API 토큰 관리 API, 로그인 사용자만 사용 가능, api-token 유저 불가능 )")
@Slf4j
@RestController
@RequestMapping("/api/v1/api-tokens")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('LEADER')")
public class ApiTokenController {

    private final ApiTokenService apiTokenService;

    @AuditLog(action = "CREATE_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 생성",
            description = "새로운 API 토큰을 생성합니다. ADMIN은 모든 부서, LEADER는 본인 부서만 가능합니다."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('LEADER') and #request.department == authentication.principal.department)")
    public ApiResponse<ApiTokenResponse> createApiToken(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody CreateApiTokenRequest request) {

        AuthenticatedUser user = AuthenticatedUser.from(principal);
        ApiTokenResponse response = apiTokenService.createApiToken(request, user);
        return ApiResponse.created(response);
    }

    @AuditLog(action = "UPDATE_API_TOKEN", resourceType = "API_TOKEN")
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

        AuthenticatedUser user = AuthenticatedUser.from(principal);
        ApiTokenResponse response = apiTokenService.updateApiToken(apiTokenId, request, user);
        return ApiResponse.ok(response);
    }

    @AuditLog(action = "DELETE_API_TOKEN", resourceType = "API_TOKEN")
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
        AuthenticatedUser user = AuthenticatedUser.from(principal);
        apiTokenService.deleteApiToken(apiTokenId, user);
        return ApiResponse.ok(null);
    }

    @AuditLog(action = "VIEW_API_TOKEN", resourceType = "API_TOKEN")
    @Operation(
            summary = "API 토큰 세부 조회",
            description = "API 토큰 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{apiTokenId}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ApiResponse<ApiTokenResponse> getApiToken(@PathVariable Long apiTokenId,
                                                     @AuthenticationPrincipal MemberPrincipal principal) {
        AuthenticatedUser user = AuthenticatedUser.from(principal);
        ApiTokenResponse response = apiTokenService.getApiToken(apiTokenId, user);
        return ApiResponse.ok(response);
    }

    // TODO 현재는 내 토큰 목록 조회로 my로 되어있지만 filtering 조건 추가해서 my, 또는 내 부서 관련된 토큰을 조회할 수 있도록 수정하기
    @AuditLog(action = "VIEW_API_TOKENS", resourceType = "API_TOKEN")
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

    @AuditLog(action = "VIEW_API_TOKENS", resourceType = "API_TOKEN")
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
