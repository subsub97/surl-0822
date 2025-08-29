package com.megastudy.surlkdh.domain.shorturl.service.port;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ShortUrlRepository {

    Optional<ShortUrl> findByShortCode(String shortCode);

    Optional<ShortUrl> findByShortUrlId(Long shortUrlId);

    Page<ShortUrl> findByDepartment(Department department, Pageable pageable);

    ShortUrl save(ShortUrl shortUrl);

    void deleteShortUrlByShortUrlId(Long shortUrlId);

    String findPcUrlByShortCode(String shortCode);

    String findMobileUrlByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);

    default void evictCache(String key) {
        // 캐시를 사용하는 구현체에서만 이 메서드를 오버라이드하여 사용
    }
}