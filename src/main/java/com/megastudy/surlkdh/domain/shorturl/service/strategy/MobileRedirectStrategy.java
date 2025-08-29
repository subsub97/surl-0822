package com.megastudy.surlkdh.domain.shorturl.service.strategy;

import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MobileRedirectStrategy implements RedirectUrlStrategy {

    private final ShortUrlRepository shortUrlRepository;

    @Override
    public String findRedirectUrl(ShortUrl shortUrl) {
        return shortUrl.getMobileUrl();
    }
}
