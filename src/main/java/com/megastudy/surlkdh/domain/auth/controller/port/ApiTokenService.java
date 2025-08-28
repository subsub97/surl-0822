package com.megastudy.surlkdh.domain.auth.controller.port;

import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ApiTokenService {
    ApiTokenResponse findByTokenValue(String tokenValue);

    void updateLastUsedAt(Long apiTokenId);

    ApiTokenResponse createApiToken(CreateApiTokenRequest request, Long creatorMemberId);

    ApiTokenResponse updateApiToken(Long apiTokenId, UpdateApiTokenRequest request, Long creatorMemberId);

    ApiTokenResponse getApiToken(Long apiTokenId, Long memberId);

    void deleteApiToken(Long apiTokenId, Long memberId);

    List<ApiTokenResponse> getApiTokensByMember(Long memberId);

    List<ApiTokenResponse> getAllApiTokens(); //ADMIN 전용

    Authentication getAuthentication(String apiTokenValue);
}
