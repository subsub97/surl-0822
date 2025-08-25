package com.megastudy.surlkdh.domain.statistics.service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua_parser.Client;
import ua_parser.Parser;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final StringRedisTemplate redisTemplate;
	private final DatabaseReader databaseReader;
	private final Parser uaParser = new Parser();

		private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

	@Async
	public void collectClickData(ShortUrl shortUrl, String ipAddress, String userAgent, String referrer) {
		try {
			String country = getCountry(ipAddress);
			String platform = getPlatform(userAgent);
			String safeReferrer = referrer != null ? referrer : "direct";

			LocalDateTime now = LocalDateTime.now();
			String hourlyKeyPart = now.format(DATE_FORMATTER);
			String secondField = String.valueOf(now.getSecond());

			String redisKey = String.format("statistics:%s:%s:%s:%s:%s",
				shortUrl.getShortCode(), hourlyKeyPart, country, platform, safeReferrer);

			redisTemplate.opsForHash().increment(redisKey, secondField, 1);

		} catch (Exception e) {
			log.error("Failed to collect click data for shortUrl: {}", shortUrl.getShortCode(), e);
		}
	}

	private String getCountry(String ipAddress) {
		if (ipAddress == null || ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
			return "local";
		}
		try {
			InetAddress ip = InetAddress.getByName(ipAddress);
			return databaseReader.country(ip).getCountry().getIsoCode();
		} catch (IOException | GeoIp2Exception e) {
			log.warn("Could not resolve country for IP: {}", ipAddress);
			return "unknown";
		}
	}

	private String getPlatform(String userAgent) {
		if (userAgent == null) {
			return "unknown";
		}
		Client c = uaParser.parse(userAgent);
		return c.os.family;
	}
}