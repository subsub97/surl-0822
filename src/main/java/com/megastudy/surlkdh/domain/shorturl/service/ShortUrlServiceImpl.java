package com.megastudy.surlkdh.domain.shorturl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.port.ShortUrlService;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import com.megastudy.surlkdh.domain.shorturl.util.ShortCodeUtil;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua_parser.Client;
import ua_parser.Parser;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

	private final ShortCodeUtil shortCodeUtil;
	private final ShortUrlRepository shortUrlRepository;
	private final StatisticsService statisticsService;
	private final Parser uaParser = new Parser();

	@Override
	@Transactional
	public ShortUrlResponse createShortUrl(CreateShortUrlRequest request, Long actorId) {
		// 생성할 때는 두가지 경우가 존재한다.

		// 1. 커스텀 ShortCode를 입력한경우
		// shortCode를 작성해서 요청한 경우 유효성 검사를 진행한다.

		// shortCodeUtil을 사용해서 Encoding 한다.

		// 통과하면 중복된 Code가 있는지 검사한다.
		if (request.getShortCode() != null) {
			Optional<ShortUrl> shortCode = shortUrlRepository.findByShortCode(request.getShortCode());
			if (shortCode.isEmpty()) {
				log.info("사용 가능한 shortCode 입니다.");
				ShortUrl shortUrl = ShortUrl.create(
					request.getShortCode(),
					request.getDepartment(),
					actorId,
					"JWT",
					request.getExpiresAt(),
					request.getNote()
				);

				ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);

				List<ShortUrlRedirectRule> shortUrlRedirectRules = request.getRedirectRules().stream()
					.map(redirectRule -> ShortUrlRedirectRule.create(
						savedShortUrl.getShortUrlId(),
						redirectRule.getDeviceType(),
						redirectRule.getTargetUrl()
					))
					.collect(Collectors.toList());

				shortUrlRepository.saveAllRedirectRules(shortUrlRedirectRules);
				return ShortUrlResponse.of(shortUrl.getShortCode());
			} else {
				throw new IllegalArgumentException("이미 사용중인 shortCode 입니다.");
			}

		}

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public String redirect(String shortCode, String userAgent, String referrer, String ipAddress) {
		ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST));

		DeviceType deviceType = parseDeviceType(userAgent);

		String targetUrl = shortUrl.getShortUrlRedirectRules().stream()
			.filter(rule -> rule.getDeviceType() == deviceType)
			.findFirst()
			.map(ShortUrlRedirectRule::getTargetUrl)
			.orElseGet(() -> shortUrl.getShortUrlRedirectRules().stream()
				.findFirst()
				.map(ShortUrlRedirectRule::getTargetUrl)
				.orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST)));

		statisticsService.collectClickData(shortUrl, ipAddress, userAgent, referrer);

		return targetUrl;
	}

	private DeviceType parseDeviceType(String userAgent) {
		if (userAgent == null) {
			return DeviceType.PC; // Default to PC
		}
		Client c = uaParser.parse(userAgent);
		String deviceFamily = c.device.family;
		String osFamily = c.os.family;

		if (deviceFamily.equalsIgnoreCase("Spider") || osFamily.contains("Bot")) {
			return DeviceType.PC; // Treat bots as PC
		}

		boolean isMobile = osFamily.matches("(?i)Android|iOS|Windows Phone|Bada|Tizen|BlackBerry");
		return isMobile ? DeviceType.MOBILE : DeviceType.PC;
	}
}