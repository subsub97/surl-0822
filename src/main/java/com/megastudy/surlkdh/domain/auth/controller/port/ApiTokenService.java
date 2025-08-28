package com.megastudy.surlkdh.domain.auth.controller.port;

import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.service.dto.AuthenticatedUser;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ApiTokenService {
    ApiTokenResponse findByTokenValue(String tokenValue);

    void updateLastUsedAt(Long apiTokenId);

    ApiTokenResponse createApiToken(CreateApiTokenRequest request, AuthenticatedUser user);

    ApiTokenResponse updateApiToken(Long apiTokenId, UpdateApiTokenRequest request, AuthenticatedUser user);

    ApiTokenResponse getApiToken(Long apiTokenId, AuthenticatedUser user);

    void deleteApiToken(Long apiTokenId, AuthenticatedUser user);

    List<ApiTokenResponse> getApiTokensByMember(Long memberId);

    List<ApiTokenResponse> getAllApiTokens(); //ADMIN 전용

    Authentication getAuthentication(String apiTokenValue);
}