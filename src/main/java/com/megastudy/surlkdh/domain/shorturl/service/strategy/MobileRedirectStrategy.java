package com.megastudy.surlkdh.domain.shorturl.service.strategy;

import org.springframework.stereotype.Component;

import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MobileRedirectStrategy implements RedirectUrlStrategy {

	private final ShortUrlRepository shortUrlRepository;

	@Override
	public String findRedirectUrl(String shortCode) {
		return shortUrlRepository.findMobileUrlByShortCode(shortCode);
	}
}
