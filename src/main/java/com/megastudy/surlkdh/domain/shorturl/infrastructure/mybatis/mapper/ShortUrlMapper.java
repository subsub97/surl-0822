package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;

@Mapper
public interface ShortUrlMapper {

	void insert(ShortUrlEntity shortUrlEntity);

	ShortUrlEntity findByShortCode(String shortCode);

	ShortUrlEntity findByShortUrlId(Long shortUrlId);

	List<ShortUrlEntity> findByDepartment(String department, Pageable pageable);

	Long countByDepartment(String department);

	void update(ShortUrlEntity shortUrlEntity);

	void deleteShortUrlById(Long shortUrlId);

	String findPcUrlByShortCode(String shortCode);

	String findMobileByShortCode(String shortCode);
}