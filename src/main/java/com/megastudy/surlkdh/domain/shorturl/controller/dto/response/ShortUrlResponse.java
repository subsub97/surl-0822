package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlResponse {

	private String shortCode;

	public static ShortUrlResponse of(String shortCode) {
		return new ShortUrlResponse(shortCode);
	}
}