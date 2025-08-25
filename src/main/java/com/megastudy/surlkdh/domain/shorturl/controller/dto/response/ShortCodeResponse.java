package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortCodeResponse {

	private String shortCode;

	public static ShortCodeResponse of(String shortCode) {
		return new ShortCodeResponse(shortCode);
	}
}