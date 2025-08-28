package com.megastudy.surlkdh.domain.shorturl.service.strategy;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedirectStrategyFactory {

	private final Map<DeviceType, RedirectUrlStrategy> redirectStrategyMap;

	// PC, Mobile 타입에 맞는 전략을 생성한다.
	public RedirectStrategyFactory(MobileRedirectStrategy mobileRedirectStrategy,
		PcRedirectStrategy pcRedirectStrategy) {

		this.redirectStrategyMap = new EnumMap<DeviceType, RedirectUrlStrategy>(DeviceType.class);

		this.redirectStrategyMap.put(DeviceType.MOBILE, mobileRedirectStrategy);
		this.redirectStrategyMap.put(DeviceType.PC, pcRedirectStrategy);
	}

	public RedirectUrlStrategy getRedirectStrategy(DeviceType deviceType) {
		log.info("DeviceType이 {} 인 클라이언트의 요청이 발생했습니다.", deviceType);
		return redirectStrategyMap.get(deviceType);
	}
}
