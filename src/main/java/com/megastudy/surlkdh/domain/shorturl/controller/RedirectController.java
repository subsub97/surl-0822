package com.megastudy.surlkdh.domain.shorturl.controller;

import com.megastudy.surlkdh.domain.shorturl.controller.port.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final ShortUrlService shortUrlService;

    @Operation(
            summary = "생성된 단축 URL로 접근 시 통계 수집 및 리다이렉션",
            description = "단축 URL과 매핑된 등록된 디바이스에 맞는 원본 URL로 리다이렉트 합니다."
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String ipAddress = getClientIp(request);
        String host = request.getRemoteHost();

        log.info("Redirecting to {}", shortCode);

        URI target = URI.create(shortUrlService.redirect(shortCode, userAgent, referrer, ipAddress, host));

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT) // 302
                .location(target)
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
