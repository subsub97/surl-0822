package com.megastudy.surlkdh.infrastructure.security.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import com.megastudy.surlkdh.infrastructure.security.dto.LoginRequest;
import com.megastudy.surlkdh.infrastructure.security.dto.LoginResponse;
import com.megastudy.surlkdh.infrastructure.security.jwt.JwtToken;
import com.megastudy.surlkdh.infrastructure.security.jwt.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private static final Integer THIRTY_DAYS = 60 * 60 * 24 * 30;

	private final ObjectMapper objectMapper;
	private final JwtTokenProvider jwtTokenProvider;

	public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper,
		JwtTokenProvider jwtTokenProvider) {
		super.setAuthenticationManager(authenticationManager);
		setFilterProcessesUrl("/api/v1/auth/login");
		setAuthenticationSuccessHandler(this::success);
		setAuthenticationFailureHandler(this::failure);
		this.objectMapper = objectMapper;
		this.jwtTokenProvider = jwtTokenProvider;

	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		try {
			if (!request.getMethod().equals("POST")) {
				throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
			}

			LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				loginRequest.getEmail(),
				loginRequest.getPassword()
			);

			return super.getAuthenticationManager().authenticate(token);
		} catch (IOException e) {
			log.error("Failed to read login request", e);
			throw new AuthenticationServiceException("Failed to read login request", e);
		}
	}

	private void success(HttpServletRequest req, HttpServletResponse res,
		Authentication auth) throws IOException, ServletException {
		MemberPrincipal member = ((MemberPrincipal)auth.getPrincipal());

		JwtToken jwtToken = jwtTokenProvider.generateToken(auth, member);
		Cookie refreshTokenCookie = createCookie("refreshToken", jwtToken.getRefreshToken());
		res.addCookie(refreshTokenCookie);

		res.setContentType("application/json;charset=UTF-8");
		res.setStatus(HttpStatus.OK.value());

		LoginResponse response = LoginResponse.success(member, jwtToken.getAccessToken());
		objectMapper.writeValue(res.getWriter(), response);
	}

	private void failure(HttpServletRequest req, HttpServletResponse res,
		AuthenticationException ex) throws IOException {
		res.setContentType("application/json;charset=UTF-8");
		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		LoginResponse response = LoginResponse.failure();

		objectMapper.writeValue(res.getWriter(), response);
	}

	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(THIRTY_DAYS);
		//cookie.setSecure(true); // HTTPS 환경에서만 쿠키 전송 (아직 요구사항 미정이라 주석처리함)
		cookie.setPath("/api/v1/refresh_token");
		return cookie;
	}
}
