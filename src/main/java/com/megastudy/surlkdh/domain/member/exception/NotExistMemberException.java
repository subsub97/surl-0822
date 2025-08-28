package com.megastudy.surlkdh.domain.member.exception;

import com.megastudy.surlkdh.common.exception.ErrorCode;

public class NotExistMemberException extends RuntimeException {
	private final ErrorCode errorCode;

	public NotExistMemberException(ErrorCode errorCode) {
		super(errorCode.message());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
