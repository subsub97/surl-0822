package com.megastudy.surlkdh.domain.shorturl.controller.port;

import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;

public interface ShortUrlService {
	ShortUrlResponse createShortUrl(CreateShortUrlRequest request, Long actorId);
}