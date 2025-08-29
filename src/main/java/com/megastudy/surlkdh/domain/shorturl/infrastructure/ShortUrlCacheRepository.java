package com.megastudy.surlkdh.domain.shorturl.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class ShortUrlCacheRepository implements ShortUrlRepository {

    private final ShortUrlRepository shortUrlDBRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "shortUrl:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(60);

    @Override
    public ShortUrl save(ShortUrl shortUrl) {
        return shortUrlDBRepository.save(shortUrl);
    }

    @Override
    public Optional<ShortUrl> findByShortCode(String shortCode) {
        String cacheKey = CACHE_KEY_PREFIX + shortCode;
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("Cache hit for shortCode: {}", shortCode);
                ShortUrl shortUrl = objectMapper.convertValue(cachedData, ShortUrl.class);
                return Optional.of(shortUrl);
            }

            log.debug("Cache miss for shortCode: {}", shortCode);
            Optional<ShortUrl> shortUrlOpt = shortUrlDBRepository.findByShortCode(shortCode);

            if (shortUrlOpt.isPresent()) {
                redisTemplate.opsForValue().set(cacheKey, shortUrlOpt.get(), CACHE_TTL);
            }
            return shortUrlOpt;

        } catch (SerializationException e) {
            log.warn("레디스 통신을 위한 직렬화 과정에서 오류가 발생했습니다. {}: {}", cacheKey, e.getMessage());
            return shortUrlDBRepository.findByShortCode(shortCode);
        }
    }

    @Override
    public void deleteByShortCode(String shortCode) {
        shortUrlDBRepository.deleteByShortCode(shortCode);
        String cacheKey = CACHE_KEY_PREFIX + shortCode;
        redisTemplate.delete(cacheKey);
        log.debug("Cache evicted for shortCode: {}", shortCode);
    }

    @Override
    public Optional<ShortUrl> findByShortUrlId(Long shortUrlId) {
        return shortUrlDBRepository.findByShortUrlId(shortUrlId);
    }

    @Override
    public Page<ShortUrl> findByDepartment(Department department, Pageable pageable) {
        return shortUrlDBRepository.findByDepartment(department, pageable);
    }

    @Override
    public void deleteShortUrlByShortUrlId(Long shortUrlId) {
        Optional<ShortUrl> shortUrlOpt = findByShortUrlId(shortUrlId);
        shortUrlDBRepository.deleteShortUrlByShortUrlId(shortUrlId);
        shortUrlOpt.ifPresent(shortUrl -> {
            String cacheKey = CACHE_KEY_PREFIX + shortUrl.getShortCode();
            redisTemplate.delete(cacheKey);
            log.debug("Cache evicted for shortCode: {}", shortUrl.getShortCode());
        });
    }

    @Override
    public void evictCache(String shortCode) {
        log.debug("단축 코드가 변경되어 이전 캐시를 삭제합니다. oldShortCode: {}", shortCode);
        String cacheKey = CACHE_KEY_PREFIX + shortCode;
        redisTemplate.delete(cacheKey);
    }

    // TODO 리팩토링 과정에서 사용 안하게 되었음 단건 조회용으로 필요할 수 있어 삭제하지 않음
    @Override
    public String findPcUrlByShortCode(String shortCode) {
        return findByShortCode(shortCode).map(ShortUrl::getPcUrl).orElse(null);
    }

    @Override
    public String findMobileUrlByShortCode(String shortCode) {
        return findByShortCode(shortCode).map(ShortUrl::getMobileUrl).orElse(null);
    }
}
