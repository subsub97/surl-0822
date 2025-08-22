package com.megastudy.surlkdh.infrastructure.security.jwt.exception;

import com.megastudy.surlkdh.common.exception.ErrorCode;

public class UnAuthorizedException extends RuntimeException {
	private final ErrorCode errorCode;

	public UnAuthorizedException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
