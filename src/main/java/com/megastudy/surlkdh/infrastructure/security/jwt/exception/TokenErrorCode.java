package com.megastudy.surlkdh.infrastructure.security.jwt.exception;

import org.springframework.http.HttpStatus;

import com.megastudy.surlkdh.common.exception.ErrorCode;

public enum TokenErrorCode implements ErrorCode {
	UNAUTHORIZED("JWT.UNAUTHORIZED", "접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
	MALFORMED("JWT.MALFORMED", "잘못된 형식의 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
	TAMPERED("JWT.TAMPERED", "위조되었거나 변조된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EXPIRED("JWT.EXPIRED", "만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
	UNSUPPORTED("JWT.UNSUPPORTED", "지원하지 않는 JWT 형식입니다.", HttpStatus.UNAUTHORIZED),
	INVALID("JWT.INVALID", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED);
	private final String code;
	private final String message;
	private final HttpStatus status;

	TokenErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public HttpStatus status() {
		return status;
	}
}
