package com.megastudy.surlkdh.domain.shorturl.controller.port;

import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;

public interface ShortUrlService {
	void createShortUrl(CreateShortUrlRequest request, Long actorId);
}
