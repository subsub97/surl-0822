package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlRedirectRuleEntity;

@Mapper
public interface ShortUrlMapper {

	void insert(ShortUrlEntity shortUrlEntity);

	ShortUrlEntity findByShortCode(String shortCode);

	void insertRedirectRules(List<ShortUrlRedirectRuleEntity> rules);

	List<ShortUrlRedirectRuleEntity> findRedirectRulesByShortUrlId(Long shortUrlId);
}