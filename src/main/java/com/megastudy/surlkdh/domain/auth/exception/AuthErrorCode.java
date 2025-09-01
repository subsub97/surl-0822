package com.megastudy.surlkdh.domain.auth.exception;

import org.springframework.http.HttpStatus;
import com.megastudy.surlkdh.common.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    AUTHENTICATION_REQUIRED("AUTH.AUTHENTICATION_REQUIRED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("AUTH.INVALID_CREDENTIALS", "잘못된 인증 정보입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH.ACCESS_DENIED", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CREATE_PERMISSION_DENIED("AUTH.CREATE_PERMISSION_DENIED", "생성 권한이 없습니다.", HttpStatus.FORBIDDEN),
    UPDATE_PERMISSION_DENIED("AUTH.UPDATE_PERMISSION_DENIED", "수정 권한이 없습니다.", HttpStatus.FORBIDDEN),
    DELETE_PERMISSION_DENIED("AUTH.DELETE_PERMISSION_DENIED", "삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
    
    // API Token 관련
    INVALID_API_TOKEN("AUTH.INVALID_API_TOKEN", "유효하지 않은 API 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_API_TOKEN("AUTH.EXPIRED_API_TOKEN", "만료된 API 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("AUTH.TOKEN_NOT_FOUND", "토큰 정보를 확인해주세요.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AuthErrorCode(String code, String message, HttpStatus status) {
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