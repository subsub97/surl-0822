package com.megastudy.surlkdh.domain.member.exception;

import com.megastudy.surlkdh.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
    NOT_FOUND("MEMBER.NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("MEMBER.INVALID_EMAIL", "유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("MEMBER.DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST),
    UPDATE_FAILED("MEMBER.UPDATE_FAILED", "사용자 정보 업데이트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    MemberErrorCode(String code, String message, HttpStatus status) {
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
