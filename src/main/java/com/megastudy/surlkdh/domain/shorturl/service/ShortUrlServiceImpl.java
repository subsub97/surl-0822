package com.megastudy.surlkdh.domain.shorturl.service;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.common.exception.RetryException;
import com.megastudy.surlkdh.domain.audit.aop.AuditContext;
import com.megastudy.surlkdh.domain.auth.entity.UserType;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.CreateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.request.UpdateShortUrlRequest;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortCodeResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.dto.response.ShortUrlResponse;
import com.megastudy.surlkdh.domain.shorturl.controller.port.ShortUrlService;
import com.megastudy.surlkdh.domain.shorturl.entity.DeviceType;
import com.megastudy.surlkdh.domain.shorturl.entity.ShortUrl;
import com.megastudy.surlkdh.domain.shorturl.service.port.ShortUrlRepository;
import com.megastudy.surlkdh.domain.shorturl.service.strategy.RedirectStrategyFactory;
import com.megastudy.surlkdh.domain.shorturl.service.strategy.RedirectUrlStrategy;
import com.megastudy.surlkdh.domain.shorturl.util.ShortCodeUtil;
import com.megastudy.surlkdh.domain.shorturl.util.snowflake.SnowflakeIdGenerator;
import com.megastudy.surlkdh.domain.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

    private static final int MAX_RANDOM_RETRIES = 3;

    private final ShortCodeUtil shortCodeUtil;
    private final ShortUrlRepository shortUrlRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final RedirectStrategyFactory redirectStrategyFactory;
    private final StatisticsService statisticsService;
    private final Parser uaParser = new Parser();
    private final AuditContext auditContext;

    @Override
    @Transactional
    public ShortCodeResponse createShortUrl(CreateShortUrlRequest request, Long actorId, UserType userType) {
        log.info("단축 URL 생성 요청 시작: pc_originalUrl={}, mobile_originalUrl={}, actorId={}, userType={}",
                request.getPcUrl(), request.getMobileUrl(), actorId, userType);

        if (request.getShortCode() != null && !request.getShortCode().isEmpty()) {
            String desiredCode = request.getShortCode();
            log.info("사용자 지정 단축 코드 시도: {}", desiredCode);
            assertAvailableThrow(desiredCode);
            ShortCodeResponse response = insert(desiredCode, request, actorId, userType);
            log.info("사용자 지정 단축 코드 {}로 단축 URL 생성 성공.", response.getShortCode());
            auditContext.setResourceId(response.getShortUrlId());
            return response;
        }

        for (int attempt = 1; attempt <= MAX_RANDOM_RETRIES; attempt++) {
            String randomCode = generateRandomCode();
            log.debug("랜덤 단축 코드 생성 시도 ({}회차): {}", attempt, randomCode);

            if (isAvailable(randomCode)) {
                try {
                    ShortCodeResponse response = insert(randomCode, request, actorId, userType);
                    log.info("랜덤 단축 코드 {}로 단축 URL 생성 성공.", response.getShortCode());
                    auditContext.setResourceId(response.getShortUrlId());
                    return response;
                } catch (DataIntegrityViolationException e) {
                    // DB 유니크 제약 조건 위반 (매우 드물게 발생 가능)
                    log.warn("단축코드 {} 생성 중 DB 중복 오류 발생 ({}회차). 재시도합니다. 오류: {}", randomCode, attempt, e.getMessage());
                    continue;
                }
            }
            log.warn("생성된 랜덤 단축 코드 {}가 이미 사용 중입니다. ({}회차)", randomCode, attempt);

            if (attempt == MAX_RANDOM_RETRIES) {
                log.error("최대 재시도 횟수({}) 도달. 단축 URL 생성에 실패했습니다. pc_originalUrl={}", MAX_RANDOM_RETRIES,
                        request.getPcUrl());
                throw new RetryException(CommonErrorCode.MAX_RETRY_ERROR);
            }
        }

        // 도달 불가능 (방어적 코드)
        log.error("예상치 못한 오류: 단축 URL 생성 로직이 도달 불가능한 코드에 도달했습니다. pc_originalUrl={}",
                request.getPcUrl());
        throw new RetryException(CommonErrorCode.MAX_RETRY_ERROR);
    }

    @Override
    @Transactional
    public ShortUrlResponse updateShortUrl(Long shortUrlId, UpdateShortUrlRequest request, Long actorId,
                                           UserType userType) {
        log.info("단축 URL 업데이트 요청 시작: shortUrlId={}, actorId={}, userType={}", shortUrlId, actorId, userType);

        ShortUrl shortUrl = shortUrlRepository.findByShortUrlId(shortUrlId)
                .orElseThrow(() -> {
                    log.warn("업데이트할 단축 URL을 찾을 수 없습니다: shortUrlId={}", shortUrlId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        log.debug("단축 URL {} 현재 정보: shortCode={}, note={}, expiresAt={}, department={}",
                shortUrlId, shortUrl.getShortCode(), shortUrl.getNote(), shortUrl.getExpiresAt(), shortUrl.getDepartment());

        String oldShortCode = shortUrl.getShortCode();
        boolean isShortCodeChanged = request.getShortCode() != null && !request.getShortCode().equals(oldShortCode);

        if (isShortCodeChanged) {
            Function<String, Boolean> isShortCodeAvailable = this::isAvailable;
            shortUrl.changeShortCode(request.getShortCode(), isShortCodeAvailable);
        }
        if (request.getNote() != null) {
            log.info("단축 URL {}의 note 변경 시도: {} -> {}", shortUrlId, shortUrl.getNote(), request.getNote());
            shortUrl.updateNote(request.getNote());
        }
        if (request.getExpiresAt() != null) {
            log.info("단축 URL {}의 만료일 변경 시도: {} -> {}", shortUrlId, shortUrl.getExpiresAt(), request.getExpiresAt());
            shortUrl.setExpiration(request.getExpiresAt());
        }
        if (request.getDepartment() != null) {
            log.info("단축 URL {}의 부서 변경 시도: {} -> {}", shortUrlId, shortUrl.getDepartment(), request.getDepartment());
            shortUrl.transferDepartment(request.getDepartment());
        }
        if (request.getPcUrl() != null) {
            log.info("단축 URL {}의 리다이렉트 원본 PC URL 변경 시도. 요청된 URL: {}", shortUrlId, request.getPcUrl());
            shortUrl.changePcUrl(request.getPcUrl());
        }
        if (request.getMobileUrl() != null) {
            log.info("단축 URL {}의 리다이렉트 원본 Mobile URL 변경 시도. 요청된 URL: {}", shortUrlId, request.getMobileUrl());
            shortUrl.changeMobileUrl(request.getMobileUrl());
        }

        ShortUrl updatedShortUrl = shortUrlRepository.save(shortUrl);
        log.info("단축 URL {} 업데이트 성공.", shortUrlId);

        if (isShortCodeChanged) {
            shortUrlRepository.evictCache(oldShortCode);
        }

        auditContext.setResourceId(updatedShortUrl.getShortUrlId());

        return ShortUrlResponse.from(updatedShortUrl);
    }

    @Override
    public ShortUrlResponse getShortUrlByShortUrlId(Long shortUrlId, Role role, Department department) {
        log.info("단축 URL 조회 요청 시작: shortUrlId={}, role={}, department={}", shortUrlId, role, department);
        auditContext.setResourceId(shortUrlId);
        ShortUrl shortUrl = shortUrlRepository.findByShortUrlId(shortUrlId)
                .orElseThrow(() -> {
                    log.warn("조회할 단축 URL을 찾을 수 없습니다: shortUrlId={}", shortUrlId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });
        validateAccessPermissions(shortUrl, role, department);
        log.info("단축 URL {} 조회 성공.", shortUrlId);
        return ShortUrlResponse.from(shortUrl);
    }

    @Override
    public Page<ShortUrlResponse> getShortUrls(Role role, Department department, Pageable pageable) {
        log.info("단축 URL 목록 조회 요청 시작: role={}, department={}, pageable={}", role, department, pageable);

        Page<ShortUrl> shortUrlsPage = shortUrlRepository.findByDepartment(department, pageable);
        log.info("단축 URL 목록 조회 성공. 총 {}개 항목 (페이지 {}/{}).",
                shortUrlsPage.getTotalElements(), shortUrlsPage.getNumber() + 1, shortUrlsPage.getTotalPages());

        return shortUrlsPage.map(ShortUrlResponse::from);
    }

    @Override
    @Transactional
    public void deleteShortUrlByShortUrlId(Long shortUrlId, Department department) {
        log.info("단축 URL 삭제 요청 시작: shortUrlId={}, department={}", shortUrlId, department);
        auditContext.setResourceId(shortUrlId);
        shortUrlRepository.findByShortUrlId(shortUrlId)
                .orElseThrow(() -> {
                    log.warn("삭제할 단축 URL을 찾을 수 없습니다: shortUrlId={}", shortUrlId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        shortUrlRepository.deleteShortUrlByShortUrlId(shortUrlId);
        log.info("단축 URL {} 삭제 성공.", shortUrlId);
    }

    @Override
    @Transactional(readOnly = true)
    public String redirect(String shortCode, String userAgent, String referrer, String ipAddress, String host) {

        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.BAD_REQUEST));

        DeviceType deviceType = parseDeviceType(userAgent);

        statisticsService.collectClickData(shortUrl, ipAddress, userAgent, referrer, host);

        //디바이스에 따라서 원본 URL 조회하기
        RedirectUrlStrategy redirectStrategy = redirectStrategyFactory.getRedirectStrategy(deviceType);

        return redirectStrategy.findRedirectUrl(shortUrl);
    }

    private String generateRandomCode() {
        long generatedId = snowflakeIdGenerator.nextId();
        String randomCode = shortCodeUtil.encode(generatedId);
        log.debug("Snowflake ID {}를 사용하여 랜덤 코드 생성: {}", generatedId, randomCode);
        return randomCode;
    }

    private boolean isAvailable(String shortCode) {
        boolean available = shortUrlRepository.findByShortCode(shortCode).isEmpty();
        log.debug("단축 코드 {} 사용 가능 여부: {}", shortCode, available);
        return available;
    }

    private void assertAvailableThrow(String shortCode) {
        if (!isAvailable(shortCode)) {
            log.warn("요청된 단축 코드 {}가 이미 사용 중입니다. 중복 오류 발생.", shortCode);
            throw new BusinessException(CommonErrorCode.DUPLICATION_ERROR);
        }
    }

    private ShortCodeResponse insert(String shortCode,
                                     CreateShortUrlRequest request,
                                     long actorId,
                                     UserType userType) {

        log.debug("단축 URL 데이터베이스 삽입 시작: shortCode={}, pc_originalUrl={}, mobile_originalUrl={}, actorId={}",
                shortCode, request.getPcUrl(), request.getMobileUrl(), actorId);

        ShortUrl shortUrl = ShortUrl.create(
                shortCode,
                request.getDepartment(),
                actorId,
                userType,
                request.getExpiresAt(),
                request.getNote(),
                request.getPcUrl(),
                request.getMobileUrl()
        );

        ShortUrl saved = shortUrlRepository.save(shortUrl);
        auditContext.setResourceId(saved.getShortUrlId());
        log.debug("단축 URL 저장 완료: shortUrlId={}", saved.getShortUrlId());

        return ShortCodeResponse.of(saved.getShortUrlId(), saved.getShortCode());
    }

    private void validateAccessPermissions(ShortUrl shortUrl, Role role, Department department) {
        log.debug("접근 권한 검증 시작: shortUrlId={}, role={}, department={}", shortUrl.getShortUrlId(), role, department);
        if (role == Role.ADMIN) {
            log.debug("관리자 권한으로 접근 허용: shortUrlId={}", shortUrl.getShortUrlId());
            return;
        }
        if (!shortUrl.getDepartment().equals(department)) {
            log.error("본인이 소속한 부서의 정보만 접근할 수 있습니다. 요청한 부서: {}, 요청자 부서: {}. shortUrlId={}",
                    shortUrl.getDepartment(), department.getName(), shortUrl.getShortUrlId());
            throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
        }
        log.debug("일반 사용자 권한으로 접근 허용: shortUrlId={}, department={}", shortUrl.getShortUrlId(), department.getName());
    }

    private DeviceType parseDeviceType(String userAgent) {
        if (userAgent == null) {
            return DeviceType.PC;
        }

        Client client = uaParser.parse(userAgent);
        String deviceFamily = client.device.family;
        String osFamily = client.os.family;

        // TODO BOT으로 분류해서 통계 데이터 신뢰도 높이기
        if (deviceFamily.equalsIgnoreCase("Spider") || osFamily.equals("Bot")) {
            return DeviceType.PC;
        }

        // (?i) 대소문가 구분 없음
        boolean isMobile = osFamily.matches("(?i)Android|iOS|Windows Phone|Bada|Tizen|BlackBerry");
        return isMobile ? DeviceType.MOBILE : DeviceType.PC;
    }
}