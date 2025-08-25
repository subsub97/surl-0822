package com.megastudy.surlkdh.domain.shorturl.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RedirectController {

	@Operation(
		summary = "생성된 단축 URL로 접근 시 통계 수집 및 리다이렉션",
		description = "단축 URL과 매핑된 등록된 디바이스에 맞는 원본 URL로 리다이렉트 합니다."
	)
	@GetMapping("/{shortCode}")
	public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
		// String redirectUrl = shortUrlService.getRedirectUrl(shortCode);
		log.info("Redirecting to {}", shortCode);
		URI target = URI.create("https://naver.com");
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY) // 301
			.location(target)
			.build();
	}
}
