package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlRedirectRuleEntity;

@Mapper
public interface ShortUrlMapper {

	void insert(ShortUrlEntity shortUrlEntity);

	ShortUrlEntity findByShortCode(String shortCode);

	ShortUrlEntity findByShortUrlId(Long shortUrlId);

	List<ShortUrlEntity> findByDepartment(String department, Pageable pageable);

	Long countByDepartment(String department);

	void insertRedirectRules(List<ShortUrlRedirectRuleEntity> rules);

	List<ShortUrlRedirectRuleEntity> findRedirectRulesByShortUrlId(Long shortUrlId);

	void update(ShortUrlEntity shortUrlEntity);

	// New method to delete redirect rules
	void deleteRedirectRulesByShortUrlId(Long shortUrlId);

	void deleteRedirectRuleById(Long redirectRuleId);
}