package com.megastudy.surlkdh.domain.statistics.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShortUrlClickResponse {

    private final Long shortUrlId;
    private final String statistics;
}
