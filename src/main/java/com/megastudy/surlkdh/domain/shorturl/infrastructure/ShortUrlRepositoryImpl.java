package com.megastudy.surlkdh.domain.shorturl.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlRedirectRuleEntity;
import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper.ShortUrlMapper;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;

import lombok.RequiredArgsConstructor;

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
	public ShortUrl save(ShortUrl shortUrl) {
		if (shortUrl.getShortUrlId() == null) {
			return insert(shortUrl);
		}

		//TODO update 구현
		return null;

	}

	@Override
	public List<ShortUrlRedirectRule> saveAllRedirectRules(List<ShortUrlRedirectRule> rules) {
		return insertRedirectRules(rules);
	}

	private ShortUrl insert(ShortUrl shortUrl) {
		ShortUrlEntity entity = ShortUrlEntity.from(shortUrl);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(LocalDateTime.now());

		shortUrlMapper.insert(entity);

		return findByShortCode(entity.getShortCode()).orElseThrow();
	}

	private List<ShortUrlRedirectRule> insertRedirectRules(List<ShortUrlRedirectRule> rules) {
		List<ShortUrlRedirectRuleEntity> ruleEntities = rules.stream()
			.map(redirectRule -> ShortUrlRedirectRuleEntity.from(redirectRule))
			.collect(Collectors.toList());

		shortUrlMapper.insertRedirectRules(ruleEntities);

		return ruleEntities.stream()
			.map(ShortUrlRedirectRuleEntity::toDomain)
			.collect(Collectors.toList());
	}
}