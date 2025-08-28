package com.megastudy.surlkdh.domain.shorturl.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper.ShortUrlMapper;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShortUrlRepositoryImpl implements ShortUrlRepository {

	private final ShortUrlMapper shortUrlMapper;

	@Override
	public Optional<ShortUrl> findByShortCode(String shortCode) {
		ShortUrlEntity entity = shortUrlMapper.findByShortCode(shortCode);
		return Optional.ofNullable(entity).map(ShortUrlEntity::toDomain);
	}

	@Override
	public Optional<ShortUrl> findByShortUrlId(Long shortUrlId) {
		ShortUrlEntity entity = shortUrlMapper.findByShortUrlId(shortUrlId);
		return Optional.ofNullable(entity).map(ShortUrlEntity::toDomain);
	}

	@Override
	public Page<ShortUrl> findByDepartment(Department department, Pageable pageable) {
		Long count = shortUrlMapper.countByDepartment(department.getName());
		List<ShortUrlEntity> entityList = shortUrlMapper.findByDepartment(department.getName(), pageable);
		List<ShortUrl> domainList = entityList.stream()
			.map(ShortUrlEntity::toDomain)
			.collect(Collectors.toList());
		return new PageImpl<>(domainList, pageable, count);
	}

	@Override
	public ShortUrl save(ShortUrl shortUrl) {
		if (shortUrl.getShortUrlId() == null) {
			return insert(shortUrl);
		}
		return update(shortUrl);
	}

	@Override
	public void deleteShortUrlByShortUrlId(Long shortUrlId) {
		shortUrlMapper.deleteShortUrlById(shortUrlId);
	}

	@Override
	public String findPcUrlByShortCode(String shortCode) {
		return shortUrlMapper.findPcUrlByShortCode(shortCode);
	}

	@Override
	public String findMobileUrlByShortCode(String shortCode) {
		return shortUrlMapper.findMobileByShortCode(shortCode);
	}

	private ShortUrl insert(ShortUrl shortUrl) {
		ShortUrlEntity entity = ShortUrlEntity.from(shortUrl);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(entity.getCreatedAt());

		shortUrlMapper.insert(entity);

		return findByShortCode(entity.getShortCode()).orElseThrow();
	}

	private ShortUrl update(ShortUrl shortUrl) {
		ShortUrlEntity entity = ShortUrlEntity.from(shortUrl);
		entity.setUpdatedAt(LocalDateTime.now());

		shortUrlMapper.update(entity);

		return findByShortUrlId(shortUrl.getShortUrlId()).orElseThrow();
	}
}