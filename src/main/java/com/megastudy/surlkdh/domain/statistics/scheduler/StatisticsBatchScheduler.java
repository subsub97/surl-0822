package com.megastudy.surlkdh.domain.statistics.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import com.megastudy.surlkdh.domain.statistics.entity.ShorturlClicksSec;
import com.megastudy.surlkdh.domain.statistics.service.port.ShorturlClicksSecRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsBatchScheduler {

	private final StringRedisTemplate redisTemplate;
	private final ShorturlClicksSecRepository statisticsRepository;
	private final ShortUrlRepository shortUrlRepository;
	private static final int BATCH_SIZE = 1000;
	private static final DateTimeFormatter HOURLY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

	@Scheduled(fixedRate = 18000) // 10초
	public void aggregateAndStoreStatistics() {
		log.info("Starting statistics batch processing...");
		Set<String> keys = redisTemplate.keys("statistics:*");
		if (keys == null || keys.isEmpty()) {
			log.info("No statistics keys to process.");
			return;
		}

		List<ShorturlClicksSec> batchList = new ArrayList<>();
		List<String> processedKeys = new ArrayList<>();

		for (String key : keys) {
			try {
				Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
				if (data.isEmpty()) {
					processedKeys.add(key);
					continue;
				}

				String[] parts = key.split(":");
				if (parts.length < 6)
					continue;

				String shortCode = parts[1];
				Long shortUrlId = shortUrlRepository.findByShortCode(shortCode)
					.map(su -> su.getShortUrlId())
					.orElse(null);
				if (shortUrlId == null) {
					log.warn("Could not find shortUrl for shortCode: {}. Skipping key: {}", shortCode, key);
					processedKeys.add(key);
					continue;
				}

				String hourlyPart = parts[2];
				log.info("Processing key: {}, shortUrlId: {}, hourlyPart: {}", key, shortUrlId, hourlyPart);
				String country = parts[3];
				String platform = parts[4];
				String referrer = String.join(":", java.util.Arrays.copyOfRange(parts, 5, parts.length));

				LocalDateTime baseTimestamp = LocalDateTime.parse(hourlyPart, HOURLY_FORMATTER)
					.withSecond(0);

				for (Map.Entry<Object, Object> entry : data.entrySet()) {
					int second = Integer.parseInt(entry.getKey().toString());
					int clicks = Integer.parseInt(entry.getValue().toString());
					LocalDateTime tsSec = baseTimestamp.withSecond(second);

					batchList.add(ShorturlClicksSec.builder()
						.shortUrlId(shortUrlId)
						.countryCode(country)
						.deviceType(platform)
						.referrer(referrer)
						.clicksCnt(clicks)
						.tsSec(tsSec)
						.build());
				}

				if (batchList.size() >= BATCH_SIZE) {
					statisticsRepository.upsertAll(batchList);
					log.info("Upserted {} statistics records.", batchList.size());
					batchList.clear();
				}
				processedKeys.add(key);
			} catch (Exception e) {
				log.error("Failed to process key: {}", key, e);
			}
		}

		if (!batchList.isEmpty()) {
			statisticsRepository.upsertAll(batchList);
			log.info("Upserted remaining {} statistics records.", batchList.size());
		}

		if (!processedKeys.isEmpty()) {
			redisTemplate.delete(processedKeys);
			log.info("Deleted {} processed keys from Redis.", processedKeys.size());
		}

		log.info("Statistics batch processing finished.");
	}
}