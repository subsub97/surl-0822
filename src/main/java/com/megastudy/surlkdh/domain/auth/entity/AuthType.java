package com.megastudy.surlkdh.domain.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {
	JWT("jwt"),
	API_TOKEN("api_token");

	private final String type;
}