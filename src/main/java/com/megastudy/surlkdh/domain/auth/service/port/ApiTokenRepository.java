package com.megastudy.surlkdh.domain.auth.service.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;

public interface ApiTokenRepository {
	Optional<ApiToken> findByTokenValue(String tokenValue);

	void updateLastUsedAt(Long apiTokenId, LocalDateTime lastUsedAt);

	ApiToken save(ApiToken apiToken);

	Optional<ApiToken> findById(Long apiTokenId);

	List<ApiToken> findByMemberId(Long memberId);

	List<ApiToken> findAll();

	void deleteById(Long apiTokenId);

	ApiToken update(ApiToken apiToken);
}