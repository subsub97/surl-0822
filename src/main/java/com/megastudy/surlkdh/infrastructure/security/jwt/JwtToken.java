package com.megastudy.surlkdh.infrastructure.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtToken {
	private final String grantType;
	private final String accessToken;
	private final String refreshToken;
}

