package com.megastudy.surlkdh.domain.statistics.service;

import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.statistics.controller.dto.request.StatisticRequest;
import com.megastudy.surlkdh.domain.statistics.controller.dto.response.StatisticsDataPoint;
import com.megastudy.surlkdh.domain.statistics.infrastructure.ShortUrlStatisticsRepositoryImpl;
import com.megastudy.surlkdh.domain.statistics.scheduler.dto.request.ShortUrlData;
import com.megastudy.surlkdh.infrastructure.geoip.GeoIpService;
import com.megastudy.surlkdh.infrastructure.geoip.dto.GeoIPResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final String REDIS_STAT_KEY = "shortUrl:stat";
    private static final String LOCALHOST_IP_V4 = "127.0.0.1";
    private static final String LOCALHOST_IP_V6 = "0:0:0:0:0:0:0:1";
    private static final String COUNTRY_LOCAL = "local";
    private static final String COUNTRY_UNKNOWN = "unknown";
    private static final String PLATFORM_UNKNOWN = "unknown";
    private static final String REFERRER_DIRECT = "direct";

    private final Parser uaParser = new Parser();
    private final GeoIpService geoIpService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ShortUrlStatisticsRepositoryImpl shortUrlStatisticsRepository;

    @Async("shortUrlDataExecutor")
    public void collectClickData(ShortUrl shortUrl, String ipAddress, String userAgent, String referrer, String host) {
        try {
            ShortUrlData shortUrlData = createShortUrlDataRequest(shortUrl, ipAddress, userAgent, referrer, host);
            redisTemplate.opsForList().leftPush(REDIS_STAT_KEY, shortUrlData);
        } catch (Exception e) {
            log.error("shortUrl 관련 정보 수집 실패 : {}", shortUrl.getShortCode(), e);
        }
    }

    public boolean processBatch(List<ShortUrlData> batch) {
        log.info("Processing {} statistics records", batch.size());
        shortUrlStatisticsRepository.processBatch(batch);
        return true;
    }

    public Page<StatisticsDataPoint> getGroupByStatistics(StatisticRequest request, Pageable pageable) {
        return shortUrlStatisticsRepository.getGroupByStatistics(request, pageable);
    }

    private ShortUrlData createShortUrlDataRequest(ShortUrl shortUrl, String ipAddress, String userAgent, String referrer, String host) {
        String country = getCountry(ipAddress);
        String platform = getPlatform(userAgent);
        String safeReferrer = referrer != null ? referrer : REFERRER_DIRECT;

        return ShortUrlData.builder()
                .shortUrlId(shortUrl.getShortUrlId())
                .countryCode(country)
                .deviceType(platform)
                .referrer(safeReferrer)
                .domain(host)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String getCountry(String ipAddress) {
        if (isLocalAddress(ipAddress)) {
            return COUNTRY_LOCAL;
        }
        try {
            GeoIPResult result = geoIpService.lookup(ipAddress);
            return result.getCountryCode() != null ? result.getCountryCode() : COUNTRY_UNKNOWN;
        } catch (Exception e) {
            log.warn("처리 불가능한 IP: {}", ipAddress);
            return COUNTRY_UNKNOWN;
        }
    }

    private boolean isLocalAddress(String ipAddress) {
        return ipAddress == null || ipAddress.equals(LOCALHOST_IP_V4) || ipAddress.equals(LOCALHOST_IP_V6);
    }

    private String getPlatform(String userAgent) {
        if (userAgent == null) {
            return PLATFORM_UNKNOWN;
        }
        Client client = uaParser.parse(userAgent);
        return client.os.family;
    }
}