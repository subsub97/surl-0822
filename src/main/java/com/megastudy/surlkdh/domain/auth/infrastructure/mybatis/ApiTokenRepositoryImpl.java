package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto.ApiTokenEntity;
import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.mapper.ApiTokenMapper;
import com.megastudy.surlkdh.domain.auth.service.port.ApiTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ApiTokenRepositoryImpl implements ApiTokenRepository {

	private final ApiTokenMapper apiTokenMapper;

	@Override
	public Optional<ApiToken> findByTokenValue(String tokenValue) {
		ApiTokenEntity entity = apiTokenMapper.findByTokenValue(tokenValue);
		return Optional.ofNullable(entity).map(ApiTokenEntity::toModel);
	}

	@Override
	public Optional<ApiToken> findById(Long apiTokenId) {
		ApiTokenEntity entity = apiTokenMapper.findById(apiTokenId);
		return Optional.ofNullable(entity).map(ApiTokenEntity::toModel);
	}

	@Override
	public List<ApiToken> findByMemberId(Long memberId) {
		List<ApiTokenEntity> entities = apiTokenMapper.findByMemberId(memberId);
		return entities.stream()
			.map(ApiTokenEntity::toModel)
			.collect(Collectors.toList());
	}

	@Override
	public List<ApiToken> findAll() {
		List<ApiTokenEntity> entities = apiTokenMapper.findAll();
		return entities.stream()
			.map(ApiTokenEntity::toModel)
			.collect(Collectors.toList());
	}

	@Override
	public ApiToken save(ApiToken apiToken) {
		if (apiToken.getApiTokenId() == null) {
			return insert(apiToken);
		} else {
			return update(apiToken);
		}
	}

	@Override
	public void updateLastUsedAt(Long apiTokenId, LocalDateTime lastUsedAt) {
		apiTokenMapper.updateLastUsedAt(apiTokenId, lastUsedAt);
	}

	@Override
	public void deleteById(Long apiTokenId) {

		apiTokenMapper.deleteById(apiTokenId);
	}

	@Override
	public ApiToken update(ApiToken apiToken) {
		ApiTokenEntity entity = ApiTokenEntity.from(apiToken);
		entity.setUpdatedAt(LocalDateTime.now());

		apiTokenMapper.update(entity);

		return findById(apiToken.getApiTokenId()).orElseThrow();
	}

	private ApiToken insert(ApiToken apiToken) {
		ApiTokenEntity entity = ApiTokenEntity.from(apiToken);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(LocalDateTime.now());

		apiTokenMapper.insert(entity);

		return findById(entity.getApiTokenId()).orElseThrow();
	}
}
