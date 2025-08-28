package com.megastudy.surlkdh.domain.shorturl.util.snowflake;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {

	// ---- 비트 구성 ----
	private static final int SEQUENCE_BITS = 12;      // 0..4095
	private static final int SERVER_BITS = 5;       // 0..31
	private static final int DC_BITS = 4;       // 0..15
	private static final int TIMESTAMP_BITS = 42;      // ms since epoch

	private static final int SERVER_SHIFT = SEQUENCE_BITS;                    // 12
	private static final int DC_SHIFT = SEQUENCE_BITS + SERVER_BITS;      // 17
	private static final int TS_SHIFT = SEQUENCE_BITS + SERVER_BITS + DC_BITS; // 21

	private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1; // 0xFFF
	private static final long SERVER_MASK = (1L << SERVER_BITS) - 1;   // 0x1F
	private static final long DC_MASK = (1L << DC_BITS) - 1;       // 0x0F
	private static final long TS_MASK = (1L << TIMESTAMP_BITS) - 1;

	private final long epochMillis;
	private final long dcId;
	private final long serverId;

	private long lastTimestamp = -1L;
	private long sequence = 0L;

	public SnowflakeIdGenerator(SnowflakeProperties props) {
		try {
			this.epochMillis = Instant.parse(props.getEpoch()).toEpochMilli();
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid epoch (ISO-8601) in config", e);
		}
		if (props.getDatacenterId() < 0 || props.getDatacenterId() > DC_MASK) {
			throw new IllegalArgumentException("datacenter-id must be 0.." + DC_MASK);
		}
		if (props.getServerId() < 0 || props.getServerId() > SERVER_MASK) {
			throw new IllegalArgumentException("server-id must be 0.." + SERVER_MASK);
		}
		this.dcId = props.getDatacenterId();
		this.serverId = props.getServerId();
	}

	public synchronized long nextId() {
		long now = currentTime();

		if (now < lastTimestamp) {
			now = waitUntil(lastTimestamp);
		}

		if (now == lastTimestamp) {
			// 같은 ms: 시퀀스 증가
			sequence = (sequence + 1) & SEQUENCE_MASK;
			if (sequence == 0) {
				// overflow -> 다음 ms까지 대기 후 시퀀스 0
				now = waitUntil(lastTimestamp + 1);
			}
		} else {
			sequence = 0;
		}

		lastTimestamp = now;

		long tsPart = (now - epochMillis) & TS_MASK; // 42비트 보존

		return (tsPart << TS_SHIFT)
			| (dcId << DC_SHIFT)
			| (serverId << SERVER_SHIFT)
			| sequence;
	}

	private long currentTime() {
		return System.currentTimeMillis();
	}

	private long waitUntil(long targetMs) {
		long now = currentTime();
		while (now < targetMs) {
			now = currentTime();
		}
		return now;
	}
}
