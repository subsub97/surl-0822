package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto.ApiTokenEntity;

@Mapper
public interface ApiTokenMapper {

	ApiTokenEntity findByTokenValue(@Param("tokenValue") String tokenValue);

	void updateLastUsedAt(@Param("apiTokenId") Long apiTokenId, @Param("lastUsedAt") LocalDateTime lastUsedAt);

	void insert(ApiTokenEntity apiTokenEntity);

	ApiTokenEntity findById(@Param("apiTokenId") Long apiTokenId);

	List<ApiTokenEntity> findByMemberId(@Param("memberId") Long memberId);

	List<ApiTokenEntity> findAll();

	void update(ApiTokenEntity apiTokenEntity);

	void deleteById(@Param("apiTokenId") Long apiTokenId);
}
