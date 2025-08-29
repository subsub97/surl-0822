package com.megastudy.surlkdh.domain.shorturl.service.strategy;

import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;

public interface RedirectUrlStrategy {

    String findRedirectUrl(ShortUrl shortUrl);
}
