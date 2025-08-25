package com.megastudy.surlkdh.domain.auth.entity;

import java.util.Arrays;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.common.exception.CommonErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
	JWT("jwt"),
	API_TOKEN("api_token");

	private final String type;

	public static UserType findByType(String type) {
		return Arrays.stream(UserType.values())
			.filter(userType -> userType.getType().equalsIgnoreCase(type))
			.findFirst()
			.orElseThrow(() -> new BusinessException(CommonErrorCode.ACCESS_DENIED));
	}
}