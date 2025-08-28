package com.megastudy.surlkdh.common.api;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final boolean success;
	private final int status;
	private final String message;
	private final T data;
	private final OffsetDateTime timestamp;

	@Builder
	private ApiResponse(boolean success, int status, String message, T data, OffsetDateTime timestamp) {
		this.success = success;
		this.status = status;
		this.message = message;
		this.data = data;
		this.timestamp = timestamp != null ? timestamp : OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
	}

	public static <T> ApiResponse<T> ok(T data) {
		return ApiResponse.<T>builder()
			.success(true).status(200).message("OK").data(data).build();
	}

	public static <T> ApiResponse<T> created(T data) {
		return ApiResponse.<T>builder()
			.success(true).status(201).message("CREATED").data(data).build();
	}

	public static ApiResponse<Void> fail(int status, String message) {
		return ApiResponse.<Void>builder()
			.success(false).status(status).message(message).build();
	}
}
