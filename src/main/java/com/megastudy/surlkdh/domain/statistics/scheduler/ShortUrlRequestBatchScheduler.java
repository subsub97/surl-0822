package com.megastudy.surlkdh.domain.statistics.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortUrlRequestBatchScheduler {

	private static final long ONE_MIN = 1000 * 60L; // 1분
	private static final String REDIS_STAT_KEY = "shortUrl:stat";
	private static final int PARALLEL_EXECUTORS = 5;

	private final RedisTemplate<String, Object> redisTemplate;
	private final AsyncBatchProcessor asyncBatchProcessor;

	@Scheduled(fixedRate = ONE_MIN)
	public void aggregateShortUrlRequests() {
		String batchTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		log.info("ShortUrl Request Batch Job started!! currentTime={}", batchTime);

		Long queueSize = redisTemplate.opsForList().size(REDIS_STAT_KEY);
		if (queueSize == null || queueSize == 0) {
			log.info("No ShortUrl Request Data to process. Exiting batch job.");
			return;
		}

		log.info("Total ShortUrl Request Data to process: {}. Distributing tasks to {} executors.", queueSize,
			PARALLEL_EXECUTORS);

		List<CompletableFuture<Void>> futures = IntStream.range(0, PARALLEL_EXECUTORS)
			.mapToObj(i -> asyncBatchProcessor.processQueueInParallel(batchTime + "_thread_" + i))
			.toList();

		// 모든 비동기 작업이 완료될 때까지 대기
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		log.info("All parallel batch tasks for {} have been dispatched.", batchTime);
	}
}
