package com.megastudy.surlkdh.infrastructure.security.jwt.exception;

import org.springframework.http.HttpStatus;

import com.megastudy.surlkdh.common.exception.ErrorCode;

public enum TokenErrorCode implements ErrorCode {
	UNAUTHORIZED("JWT.NO_AUTH_INFO", "접근 권한이 없습니다.", HttpStatus.UNAUTHORIZED),
	MALFORMED("JWT.MALFORMED", "잘못된 형식의 토큰입니다.", HttpStatus.UNAUTHORIZED),
	TAMPERED("JWT.TAMPERED", "위조되었거나 변조된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EXPIRED("JWT.EXPIRED", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
	UNSUPPORTED("JWT.UNSUPPORTED", "지원하지 않는 JWT 형식입니다.", HttpStatus.UNAUTHORIZED),
	INVALID_JWT_TOKEN("COMMON.INVALID_JWT_TOKEN", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
	INVALID_API_TOKEN("COMMON.INVALID_API_TOKEN", "유효하지 않은 API 토큰입니다.", HttpStatus.UNAUTHORIZED),
	EXPIRED_API_TOKEN("COMMON.EXPIRED_API_TOKEN", "만료된 API 토큰입니다.", HttpStatus.UNAUTHORIZED),
	AUTHENTICATION_REQUIRED("COMMON.AUTHENTICATION_REQUIRED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
	CREATE_PERMISSION_DENIED("COMMON.CREATE_PERMISSION_DENIED", "생성 권한이 없습니다.", HttpStatus.FORBIDDEN),
	NOT_FOUND("TOKEN.NOT_FOUND", "토큰 정보를 확인해주세요.", HttpStatus.NOT_FOUND);
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
