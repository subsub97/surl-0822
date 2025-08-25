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
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlRedirectRuleEntity;
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
		if (shortUrl.getCreatedAt() == null || shortUrl.getCreatedAt().equals(shortUrl.getUpdatedAt())) {
			return insert(shortUrl);
		}

		//TODO update 구현
		else {
			return update(shortUrl);
		}
	}

	@Override
	public List<ShortUrlRedirectRule> saveAllRedirectRules(List<ShortUrlRedirectRule> rules) {
		return insertRedirectRules(rules);
	}

	@Override
	public void deleteRedirectRulesByShortUrlId(Long shortUrlId) {
		shortUrlMapper.deleteRedirectRulesByShortUrlId(shortUrlId);
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

	private List<ShortUrlRedirectRule> insertRedirectRules(List<ShortUrlRedirectRule> rules) {
		List<ShortUrlRedirectRuleEntity> ruleEntities = rules.stream()
			.map(ShortUrlRedirectRuleEntity::from)
			.collect(Collectors.toList());

		shortUrlMapper.insertRedirectRules(ruleEntities);

		return ruleEntities.stream()
			.map(ShortUrlRedirectRuleEntity::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public List<ShortUrlRedirectRule> findRedirectRulesByShortUrlId(Long shortUrlId) {
		return shortUrlMapper.findRedirectRulesByShortUrlId(shortUrlId).stream()
			.map(ShortUrlRedirectRuleEntity::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void deleteRedirectRuleById(Long redirectRuleId) {
		shortUrlMapper.deleteRedirectRuleById(redirectRuleId);
	}
}