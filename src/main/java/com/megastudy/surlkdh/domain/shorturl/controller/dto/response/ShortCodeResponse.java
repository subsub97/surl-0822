package com.megastudy.surlkdh.domain.shorturl.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortCodeResponse {

    private Long shortUrlId;
    private String shortCode;

    public static ShortCodeResponse of(Long shortUrlId, String shortCode) {
        return new ShortCodeResponse(shortUrlId, shortCode);
    }
}