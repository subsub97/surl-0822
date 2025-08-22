package com.megastudy.surlkdh.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	String code();

	String message();

	HttpStatus status();
}