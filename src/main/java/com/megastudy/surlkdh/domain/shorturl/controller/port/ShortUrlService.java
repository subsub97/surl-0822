package com.megastudy.surlkdh.domain.shorturl.controller.port;

import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.UpdateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortCodeResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortUrlService {
    ShortCodeResponse createShortUrl(CreateShortUrlRequest request, Long actorId, UserType userType);

    ShortUrlResponse updateShortUrl(Long shortUrlId, UpdateShortUrlRequest request, Long actorId, UserType userType);

    ShortUrlResponse getShortUrlByShortUrlId(Long shortUrlId, Role role, Department department);

    Page<ShortUrlResponse> getShortUrls(Role role, Department department, Pageable pageable);

    void deleteShortUrlByShortUrlId(Long shortUrlId, Department department);

    String redirect(String shortCode, String userAgent, String referrer, String ipAddress, String host);

}