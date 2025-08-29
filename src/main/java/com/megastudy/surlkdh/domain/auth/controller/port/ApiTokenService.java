package com.megastudy.surlkdh.domain.auth.controller.port;

import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.service.dto.AuthenticatedMember;
import com.megastudy.surlkdh.domain.member.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ApiTokenService {
    ApiTokenResponse findByTokenValue(String tokenValue);

    ApiTokenResponse createApiToken(CreateApiTokenRequest request, AuthenticatedMember authenticatedMember);

    ApiTokenResponse updateApiToken(Long apiTokenId, UpdateApiTokenRequest request, AuthenticatedMember authenticatedMember);

    ApiTokenResponse getApiToken(Long apiTokenId, AuthenticatedMember authenticatedMember);

    void deleteApiToken(Long apiTokenId, AuthenticatedMember authenticatedMember);

    Page<ApiTokenResponse> getApiTokensByMember(Long memberId, Pageable pageable);

    Page<ApiTokenResponse> getAllApiTokens(Pageable pageable); //ADMIN 전용

    Authentication getAuthentication(String apiTokenValue);

    Page<ApiTokenResponse> getApiTokensByDepartment(Department department, Pageable pageable);
}
