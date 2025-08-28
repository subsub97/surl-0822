package com.megastudy.surlkdh.infrastructure.security.filter;

import com.megastudy.surlkdh.domain.auth.controller.port.ApiTokenService;
import com.megastudy.surlkdh.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    public static final String API_TOKEN_HEADER = "api-token";
    public static final String JWT_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TARGET_PREFIX = "/api/v1/";

    private final ApiTokenService apiTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/api-tokens")) {
            return true;
        }
        return !path.startsWith(TARGET_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication authentication = extractAuthentication(request);

            if (authentication == null) {
                log.debug("No authentication found in request headers.");
            } else {
                log.debug("Authentication found: {}", authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("Authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private Authentication extractAuthentication(HttpServletRequest request) {
        String apiToken = request.getHeader(API_TOKEN_HEADER);

        if (StringUtils.hasText(apiToken)) {
            return apiTokenService.getAuthentication(apiToken);
        }

        String jwtToken = request.getHeader(JWT_HEADER);

        if (StringUtils.hasText(jwtToken)) {
            jwtToken = jwtToken.substring(BEARER_PREFIX.length());
            return jwtTokenProvider.getAuthentication(jwtToken);
        }

        return null;
    }

}