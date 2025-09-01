package com.megastudy.surlkdh.domain.shorturl.exception;

import org.springframework.http.HttpStatus;
import com.megastudy.surlkdh.common.exception.ErrorCode;

public enum ShortUrlErrorCode implements ErrorCode {
    NOT_FOUND("SHORT_URL.NOT_FOUND", "단축 URL을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_URL("SHORT_URL.INVALID_URL", "유효하지 않은 URL입니다.", HttpStatus.BAD_REQUEST),
    EXPIRED("SHORT_URL.EXPIRED", "만료된 단축 URL입니다.", HttpStatus.GONE),
    DISABLED("SHORT_URL.DISABLED", "비활성화된 단축 URL입니다.", HttpStatus.FORBIDDEN),
    DUPLICATION_ERROR("SHORT_URL.DUPLICATION_ERROR", "요청하신 단축 URL은 사용할 수 없습니다.", HttpStatus.CONFLICT),
    MAX_RETRY_ERROR("SHORT_URL.MAX_RETRY_ERROR", "단축 URL 생성에 실패했습니다. 잠시후 재시도 해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    CREATE_FAILED("SHORT_URL.CREATE_FAILED", "단축 URL 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UPDATE_FAILED("SHORT_URL.UPDATE_FAILED", "단축 URL 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_FAILED("SHORT_URL.DELETE_FAILED", "단축 URL 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ShortUrlErrorCode(String code, String message, HttpStatus status) {
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