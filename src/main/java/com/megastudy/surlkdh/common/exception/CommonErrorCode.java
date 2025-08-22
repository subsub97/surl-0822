package com.megastudy.surlkdh.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
	BAD_REQUEST("COMMON.BAD_REQUEST", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
	ACCESS_DENIED("COMMON.ACCESS_DENIED", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	FORBIDDEN_UPDATE("COMMON.MODIFIED_DENIED", "수정 권한이 없습니다.", HttpStatus.FORBIDDEN),
	FORBIDDEN_DELETE("COMMON.DELETE_DENIED", "삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
	SERVER_ERROR("COMMON.SERVER_ERROR", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;

	CommonErrorCode(String code, String message, HttpStatus status) {
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
