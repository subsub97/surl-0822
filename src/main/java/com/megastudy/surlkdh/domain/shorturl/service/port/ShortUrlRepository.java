package com.megastudy.surlkdh.domain.shorturl.service.port;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;

public interface ShortUrlRepository {

	Optional<ShortUrl> findByShortCode(String shortCode);

	Optional<ShortUrl> findByShortUrlId(Long shortUrlId);

	Page<ShortUrl> findByDepartment(Department department, Pageable pageable);

	ShortUrl save(ShortUrl shortUrl);

	void deleteShortUrlByShortUrlId(Long shortUrlId);

	String findPcUrlByShortCode(String shortCode);

	String findMobileUrlByShortCode(String shortCode);
}