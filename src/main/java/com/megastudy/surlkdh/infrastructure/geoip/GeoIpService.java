package com.megastudy.surlkdh.infrastructure.geoip;

import com.maxmind.db.Reader;
import com.megastudy.surlkdh.infrastructure.geoip.dto.GeoIPResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoIpService {

    private final Reader reader;

    public GeoIPResult lookup(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            Map<String, Object> data = reader.get(addr, Map.class);

            if (data == null) {
                return GeoIPResult.empty();
            }

            // 실제 반환된 데이터 구조에 맞춰 값을 추출하도록 로직 수정
            String country = safeGetString(data, "country_code");
            String city = null; // 기본값 null
            if (data.containsKey("city")) {
                Object cityObj = data.get("city");
                if (cityObj instanceof Map) {
                    city = safeGetString((Map<String, Object>) cityObj, "name");
                } else {
                    city = String.valueOf(cityObj);
                }
            }

            Double lat = null;
            Double lon = null;
            if (data.containsKey("location")) {
                Map<String, Object> locationMap = (Map<String, Object>) data.get("location");
                if (locationMap != null) {
                    lat = safeGetDouble(locationMap, "latitude");
                    lon = safeGetDouble(locationMap, "longitude");
                }
            }

            String asn = safeGetString(data, "asn");
            String org = safeGetString(data, "as_name");

            return new GeoIPResult(country, city, lat, lon, asn, org);

        } catch (IOException e) {
            log.error("GeoIP 데이터베이스 접근 오류: {}", e.getMessage());
            return GeoIPResult.empty();
        } catch (Exception e) {
            log.error("GeoIP 조회 중 예상치 못한 오류 발생 : {}", e.getMessage());
            return GeoIPResult.empty();
        }
    }

    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    private Double safeGetDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
