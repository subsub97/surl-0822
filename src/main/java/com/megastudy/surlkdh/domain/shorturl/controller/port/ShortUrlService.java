package com.megastudy.surlkdh.domain.shorturl.controller.port;

import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;

public interface ShortUrlService {
	ShortUrlResponse createShortUrl(CreateShortUrlRequest request, Long actorId);

	String redirect(String shortCode, String userAgent, String referrer, String ipAddress);
}