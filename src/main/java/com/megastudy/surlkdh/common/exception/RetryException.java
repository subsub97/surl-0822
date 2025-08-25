package com.megastudy.surlkdh.common.exception;

public class RetryException extends RuntimeException {
	private final ErrorCode errorCode;

	public RetryException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
