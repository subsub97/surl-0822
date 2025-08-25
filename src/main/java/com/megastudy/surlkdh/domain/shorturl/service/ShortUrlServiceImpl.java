package com.megastudy.surlkdh.domain.shorturl.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.common.exception.RetryException;
import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.ShortUrlRedirectRuleRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.UpdateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortCodeResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.port.ShortUrlService;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrlRedirectRule;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import com.megastudy.surlkdh.domain.shorturl.util.ShortCodeUtil;
import com.megastudy.surlkdh.domain.shorturl.util.snowflake.SnowflakeIdGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

	private static final int MAX_RANDOM_RETRIES = 3;

	private final ShortCodeUtil shortCodeUtil;
	private final ShortUrlRepository shortUrlRepository;
	private final SnowflakeIdGenerator snowflakeIdGenerator;

	@Override
	@Transactional
	public ShortCodeResponse createShortUrl(CreateShortUrlRequest request, Long actorId, UserType userType) {

		if (request.getShortCode() != null && !request.getShortCode().isEmpty()) {
			String desiredCode = request.getShortCode();
			assertAvailableThrow(desiredCode);
			return insert(desiredCode, request, actorId, userType);
		}

		for (int attempt = 1; attempt <= MAX_RANDOM_RETRIES; attempt++) {
			String randomCode = generateRandomCode();

			if (isAvailable(randomCode)) {
				try {
					return insert(randomCode, request, actorId, userType);
				} catch (DataIntegrityViolationException e) {
					log.warn("단축코드 생성에 {}회 실패했습니다.", attempt);
					continue;
				}
			}

			if (attempt == MAX_RANDOM_RETRIES) {
				throw new RetryException(CommonErrorCode.MAX_RETRY_ERROR);
			}
		}

		// 도달 불가능
		throw new RetryException(CommonErrorCode.MAX_RETRY_ERROR);
	}

	@Override
	@Transactional
	public ShortUrlResponse updateShortUrl(Long shortUrlId, UpdateShortUrlRequest request, Long actorId,
		UserType userType) {
		ShortUrl shortUrl = shortUrlRepository.findByShortUrlId(shortUrlId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST));

		Function<String, Boolean> isShortCodeAvailable = this::isAvailable;

		shortUrl.update(request.getShortCode(), request.getNote(), request.getExpiresAt(), request.getDepartment(),
			isShortCodeAvailable);

		ShortUrl updatedShortUrl = shortUrlRepository.save(shortUrl);

		if (request.getRedirectRules() != null) {
			List<ShortUrlRedirectRule> existingRules = shortUrlRepository.findRedirectRulesByShortUrlId(shortUrlId);
			Map<DeviceType, ShortUrlRedirectRule> existingRulesMap = existingRules.stream()
				.collect(Collectors.toMap(ShortUrlRedirectRule::getDeviceType, Function.identity()));

			List<ShortUrlRedirectRule> rulesToSave = new java.util.ArrayList<>();
			List<Long> ruleIdsToDelete = new java.util.ArrayList<>();

			// Process new rules from request
			for (ShortUrlRedirectRuleRequest newRuleRequest : request.getRedirectRules()) {
				DeviceType deviceType = newRuleRequest.getDeviceType();
				String newTargetUrl = newRuleRequest.getTargetUrl();

				ShortUrlRedirectRule existingRule = existingRulesMap.get(deviceType);

				if (existingRule == null) {

					rulesToSave.add(
						ShortUrlRedirectRule.create(updatedShortUrl.getShortUrlId(), deviceType, newTargetUrl));
				} else {

					if (!existingRule.getTargetUrl().equals(newTargetUrl)) {

						ruleIdsToDelete.add(existingRule.getRedirectRuleId());
						rulesToSave.add(
							ShortUrlRedirectRule.create(updatedShortUrl.getShortUrlId(), deviceType, newTargetUrl));
					}

					existingRulesMap.remove(deviceType);
				}
			}

			existingRulesMap.values().forEach(rule -> ruleIdsToDelete.add(rule.getRedirectRuleId()));

			if (!ruleIdsToDelete.isEmpty()) {
				for (Long id : ruleIdsToDelete) {
					shortUrlRepository.deleteRedirectRuleById(id);
				}
			}

			if (!rulesToSave.isEmpty()) {
				shortUrlRepository.saveAllRedirectRules(rulesToSave);
			}
		}

		return ShortUrlResponse.from(updatedShortUrl);
	}

	@Override
	public ShortUrlResponse getShortUrlByShortUrlId(Long shortUrlId, Role role, Department department) {

		ShortUrl shortUrl = shortUrlRepository.findByShortUrlId(shortUrlId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST));
		validateAccessPermissions(shortUrl, role, department);
		return ShortUrlResponse.from(shortUrl);
	}

	private String generateRandomCode() {
		long generatedId = snowflakeIdGenerator.nextId();
		return shortCodeUtil.encode(generatedId);
	}

	private boolean isAvailable(String shortCode) {
		return shortUrlRepository.findByShortCode(shortCode).isEmpty();
	}

	private void assertAvailableThrow(String shortCode) {
		if (!isAvailable(shortCode)) {
			throw new BusinessException(CommonErrorCode.DUPLICATION_ERROR);
		}

	}

	@Override
	public Page<ShortUrlResponse> getShortUrls(Role role, Department department, Pageable pageable) {
		// 부서명으로 조회하기
		Page<ShortUrl> shortUrlsPage = shortUrlRepository.findByDepartment(department, pageable);

		return shortUrlsPage.map(ShortUrlResponse::from);
	}

	private ShortCodeResponse insert(String shortCode,
		CreateShortUrlRequest request,
		long actorId,
		UserType userType) {
		ShortUrl shortUrl = ShortUrl.create(
			shortCode,
			request.getDepartment(),
			actorId,
			userType,
			request.getExpiresAt(),
			request.getNote()
		);

		ShortUrl saved = shortUrlRepository.save(shortUrl);

		List<ShortUrlRedirectRule> rules = request.getRedirectRules().stream()
			.map(r -> ShortUrlRedirectRule.create(
				saved.getShortUrlId(),
				r.getDeviceType(),
				r.getTargetUrl()
			))
			.collect(Collectors.toList());

		shortUrlRepository.saveAllRedirectRules(rules);

		return ShortCodeResponse.of(saved.getShortCode());
	}

	private void validateAccessPermissions(ShortUrl shortUrl, Role role, Department department) {
		if (role == Role.ADMIN) {
			return;
		}
		if (!shortUrl.getDepartment().equals(department)) {
			log.error("본인이 소속한 부서의 정보만 접근할 수 있습니다. 요청한 부서: {}, 요청자 부서: {}",
				shortUrl.getDepartment(), department.getName());
			throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
		}
	}
}