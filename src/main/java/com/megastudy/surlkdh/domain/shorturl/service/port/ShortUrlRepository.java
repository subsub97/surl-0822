package com.megastudy.surlkdh.domain.shorturl.service.port;

import java.util.List;
import java.util.Optional;

import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;

public interface ShortUrlRepository {

	Optional<ShortUrl> findByShortCode(String shortCode);

	ShortUrl save(ShortUrl shortUrl);

	List<ShortUrlRedirectRule> saveAllRedirectRules(List<ShortUrlRedirectRule> rules);
}