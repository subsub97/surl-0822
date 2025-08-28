package com.megastudy.surlkdh.infrastructure.geoip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GeoIPResult {
    private final String countryCode;
    private final String city;
    private final Double latitude;
    private final Double longitude;
    private final String ans;
    private final String asOrg;

    public static GeoIPResult empty() {
        return GeoIPResult.builder()
                .countryCode(null)
                .city(null)
                .latitude(null)
                .longitude(null)
                .ans(null)
                .asOrg(null)
                .build();
    }
}
