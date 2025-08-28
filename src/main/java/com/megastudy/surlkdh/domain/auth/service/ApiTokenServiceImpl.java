package com.megastudy.surlkdh.domain.auth.service;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;
import com.megastudy.surlkdh.domain.audit.aop.AuditContext;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.CreateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.UpdateApiTokenRequest;
import com.megastudy.surlkdh.domain.auth.controller.dto.response.ApiTokenResponse;
import com.megastudy.surlkdh.domain.auth.controller.port.ApiTokenService;
import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.auth.service.port.ApiTokenRepository;
import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.member.service.port.MemberRepository;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import com.megastudy.surlkdh.infrastructure.security.jwt.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.megastudy.surlkdh.domain.auth.entity.UserType.API_TOKEN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ApiTokenServiceImpl implements ApiTokenService {
    private final ApiTokenRepository apiTokenRepository;
    private final MemberRepository memberRepository;
    private final RoleHierarchy roleHierarchy;
    private final AuditContext auditContext;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public ApiTokenResponse findByTokenValue(String tokenValue) {
        String hashedValue = hashToken(tokenValue);
        ApiToken apiToken = apiTokenRepository.findByTokenValue(hashedValue).orElseThrow(() -> {
            log.error("ApiToken not found for value: {}", hashedValue);
            return new BusinessException(TokenErrorCode.NOT_FOUND);
        });

        auditContext.setResourceId(apiToken.getApiTokenId());
        return ApiTokenResponse.of(apiToken, true);
    }

    @Override
    @Transactional
    public void updateLastUsedAt(Long apiTokenId) {
        auditContext.setResourceId(apiTokenId);
        log.info("Updating last used time for ApiToken id: {}", apiTokenId);
        apiTokenRepository.updateLastUsedAt(apiTokenId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public ApiTokenResponse createApiToken(CreateApiTokenRequest request, Long creatorMemberId) {
        log.info("Attempting to create ApiToken by memberId: {}", creatorMemberId);
        Member creator = memberRepository.findById(creatorMemberId)
                .orElseThrow(() -> {
                    log.error("Creator member not found for id: {}", creatorMemberId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        validateRoleAssignment(request, creator);

        String rawToken = generateRawToken();
        String hashedToken = hashToken(rawToken);

        ApiToken apiToken = ApiToken.create(
                creatorMemberId,
                request.getTokenName(),
                hashedToken,
                request.getExpiresAt(),
                request.getRole(),
                request.getDepartment()
        );

        ApiToken savedToken = apiTokenRepository.save(apiToken);
        log.info("Successfully created ApiToken id: {} for memberId: {}", savedToken.getApiTokenId(), creatorMemberId);

        auditContext.setResourceId(savedToken.getApiTokenId());

        ApiToken tokenWithRawValue = ApiToken.builder()
                .apiTokenId(savedToken.getApiTokenId())
                .memberId(savedToken.getMemberId())
                .tokenName(savedToken.getTokenName())
                .tokenValue(hashedToken)
                .expiresAt(savedToken.getExpiresAt())
                .lastUsedAt(savedToken.getLastUsedAt())
                .role(savedToken.getRole())
                .department(savedToken.getDepartment())
                .createdAt(savedToken.getCreatedAt())
                .updatedAt(savedToken.getUpdatedAt())
                .deletedAt(savedToken.getDeletedAt())
                .build();

        return ApiTokenResponse.of(tokenWithRawValue, true);
    }

    @Override
    @Transactional
    public ApiTokenResponse updateApiToken(Long apiTokenId, UpdateApiTokenRequest request, Long memberId) {
        log.info("Attempting to update ApiToken id: {} by memberId: {}", apiTokenId, memberId);
        auditContext.setResourceId(apiTokenId);
        ApiToken apiToken = apiTokenRepository.findById(apiTokenId)
                .orElseThrow(() -> {
                    log.error("ApiToken not found for id: {}", apiTokenId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found for id: {}", memberId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        validateUpdatePermissions(apiToken, member);

        ApiToken updatedToken = ApiToken.builder()
                .apiTokenId(apiToken.getApiTokenId())
                .memberId(apiToken.getMemberId())
                .tokenName(request.getTokenName() != null ? request.getTokenName() : apiToken.getTokenName())
                .tokenValue(apiToken.getTokenValue())
                .expiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : apiToken.getExpiresAt())
                .lastUsedAt(apiToken.getLastUsedAt())
                .role(request.getRole() != null ? request.getRole() : apiToken.getRole())
                .department(request.getDepartment() != null ? request.getDepartment() :
                        apiToken.getDepartment()) // Added
                .createdAt(apiToken.getCreatedAt())
                .updatedAt(apiToken.getUpdatedAt())
                .deletedAt(apiToken.getDeletedAt())
                .build();

        ApiToken savedToken = apiTokenRepository.update(updatedToken);
        log.info("Successfully updated ApiToken id: {}", savedToken.getApiTokenId());
        return ApiTokenResponse.of(savedToken, false);
    }

    @Override
    public ApiTokenResponse getApiToken(Long apiTokenId, Long memberId) {
        log.info("Fetching ApiToken id: {}", apiTokenId);
        auditContext.setResourceId(apiTokenId);
        ApiToken apiToken = apiTokenRepository.findById(apiTokenId)
                .orElseThrow(() -> {
                    log.error("ApiToken not found for id: {}", apiTokenId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found for id: {}", memberId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        validateAccessPermissions(apiToken, member);

        return ApiTokenResponse.of(apiToken, true);
    }

    @Override
    public void deleteApiToken(Long apiTokenId, Long memberId) {
        log.info("Attempting to delete ApiToken id: {} by memberId: {}", apiTokenId, memberId);
        auditContext.setResourceId(apiTokenId);
        ApiToken apiToken = apiTokenRepository.findById(apiTokenId)
                .orElseThrow(() -> {
                    log.error("ApiToken not found for id: {}", apiTokenId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("Member not found for id: {}", memberId);
                    return new BusinessException(CommonErrorCode.BAD_REQUEST);
                });

        validateUpdatePermissions(apiToken, member);

        apiTokenRepository.deleteById(apiTokenId);
        log.info("Successfully deleted ApiToken id: {}", apiTokenId);
    }

    @Override
    public List<ApiTokenResponse> getApiTokensByMember(Long memberId) {
        log.info("Fetching all ApiTokens for memberId: {}", memberId);
        List<ApiToken> tokens = apiTokenRepository.findByMemberId(memberId);
        return tokens.stream()
                .map(token -> ApiTokenResponse.of(token, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiTokenResponse> getAllApiTokens() {
        log.info("Fetching all ApiTokens");
        List<ApiToken> tokens = apiTokenRepository.findAll();
        return tokens.stream()
                .map(token -> ApiTokenResponse.of(token, false))
                .collect(Collectors.toList());
    }

    @Override
    public Authentication getAuthentication(String apiTokenValue) {
        ApiToken apiToken = apiTokenRepository.findByTokenValue(apiTokenValue)
                .orElseThrow(() -> {
                    log.error("ApiToken not found for value: {}", apiTokenValue);
                    return new BusinessException(TokenErrorCode.NOT_FOUND);
                });

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(apiToken.getRole().getKey()));

        MemberPrincipal memberPrincipal = MemberPrincipal.of(
                apiToken.getApiTokenId(),
                API_TOKEN.getType(),
                apiToken.getDepartment(),
                authorities
        );

        return new UsernamePasswordAuthenticationToken(memberPrincipal, apiTokenValue, authorities);
    }

    private void validateRoleAssignment(CreateApiTokenRequest request, Member creator) {
        Collection<GrantedAuthority> creatorAuthorities = List.of(
                new SimpleGrantedAuthority(creator.getRole().getKey())
        );

        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(
                creatorAuthorities);

        GrantedAuthority targetAuthority = new SimpleGrantedAuthority(request.getRole().getKey());

        if (!reachableGrantedAuthorities.contains(targetAuthority)) {
            log.error("자신보다 높거나 같은 등급의 토큰을 생성할 수 없습니다. 요청 등급: {}", request.getRole().getKey());
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);
        }
    }

    private void validateUpdatePermissions(ApiToken apiToken, Member member) {
        if (member.getRole() == Role.ADMIN) {
            return;
        }
        if (!apiToken.getMemberId().equals(member.getMemberId())) {
            log.error("본인이 생성한 토큰만 수정할 수 있습니다. 요청한 멤버 ID: {}, 토큰 소유자 ID: {}", member.getMemberId(),
                    apiToken.getMemberId());
            throw new BusinessException(CommonErrorCode.FORBIDDEN_UPDATE);
        }
    }

    private void validateAccessPermissions(ApiToken apiToken, Member member) {
        if (member.getRole() == Role.ADMIN) {
            return;
        }
        if (!apiToken.getDepartment().equals(member.getDepartment())) {
            log.error("본인이 소속한 부서의 토큰만 접근할 수 있습니다. 요청한 부서: {}, 토큰 소유자 부서: {}",
                    member.getDepartment(), apiToken.getDepartment());
            throw new BusinessException(CommonErrorCode.ACCESS_DENIED);
        }
    }

    private String generateRawToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new BusinessException(CommonErrorCode.SERVER_ERROR);
        }
    }
}