package com.megastudy.surlkdh.common.exception;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.infrastructure.security.jwt.exception.TokenErrorCode;
import com.megastudy.surlkdh.domain.auth.exception.AuthErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
		TokenErrorCode code;
		if (ex instanceof ExpiredJwtException) {
			code = TokenErrorCode.EXPIRED;
		} else if (ex instanceof MalformedJwtException) {
			code = TokenErrorCode.MALFORMED;
		} else if (ex instanceof UnsupportedJwtException) {
			code = TokenErrorCode.UNSUPPORTED;
		} else {
			code = TokenErrorCode.INVALID;
		}
		return buildErrorResponse(code);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpServletRequest request) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		String message = joinFieldErrors(fieldErrors);
		log.warn("Validation failed: uri={}, errors={}", request.getRequestURI(), message);
		return buildErrorResponse(CommonErrorCode.BAD_REQUEST, message);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error("IllegalArgumentException occurred: {}", ex.getMessage());
		return buildErrorResponse(CommonErrorCode.BAD_REQUEST, ex.getMessage());
	}


	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
		log.error("BusinessException occurred: {}", ex.getErrorCode().message());
		return buildErrorResponse(ex.getErrorCode());
	}

	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception ex) {
		log.warn("Access denied: {}", ex.getMessage());
		return buildErrorResponse(AuthErrorCode.ACCESS_DENIED);
	}

	@ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthException(Exception ex) {
		log.warn("Authentication failed: {}", ex.getMessage());
		return buildErrorResponse(AuthErrorCode.AUTHENTICATION_REQUIRED);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		log.error("HttpMessageNotReadableException occurred: {}", ex.getMessage());
		return buildErrorResponse(CommonErrorCode.BAD_REQUEST, "잘못된 요청 형식입니다.");
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
		ApiResponse<Void> apiResponse = ApiResponse.fail(errorCode.status().value(), errorCode.message());
		return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode, String message) {
		ApiResponse<Void> apiResponse = ApiResponse.fail(errorCode.status().value(), message);
		return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
	}

	private String joinFieldErrors(List<FieldError> errors) {
		return errors.stream()
			.filter(Objects::nonNull)
			.map(this::formatFieldError)
			.collect(Collectors.joining("; "));
	}

	private String formatFieldError(FieldError fe) {
		String field = fe.getField();
		String msg = fe.getDefaultMessage();
		return field + " (" + msg + ")";
	}
}
