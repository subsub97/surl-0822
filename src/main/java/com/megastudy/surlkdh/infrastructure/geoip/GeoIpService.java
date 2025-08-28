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

            Map<String, Object> data = (Map<String, Object>) reader.get(addr, InetAddress.class);
            if (data == null) return GeoIPResult.empty();

            String country = getIn(data, "country", "iso_code");
            String city = getIn(data, "city", "names", "en");
            Double lat = getDoubleIn(data, "location", "latitude");
            Double lon = getDoubleIn(data, "location", "longitude");
            String asn = getIn(data, "autonomous_system_number");
            String org = getIn(data, "autonomous_system_organization");

            return new GeoIPResult(country, city, lat, lon, asn, org);
        } catch (IOException e) {
            log.error("GeoIP 데이터베이스 접근 오류: {}", e.getMessage());
            return GeoIPResult.empty();
        } catch (Exception e) {
            log.error("GeoIP 조회 중 예상치 못한 오류 발생 : {}", e.getMessage());
            return GeoIPResult.empty();
        }
    }

    private String getIn(Map<String, Object> m, String... path) {
        Object cur = m;
        for (String p : path) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return (cur instanceof String) ? (String) cur : String.valueOf(cur);
    }

    private Double getDoubleIn(Map<String, Object> m, String... path) {
        Object cur = m;
        for (String p : path) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        if (cur instanceof Number) return ((Number) cur).doubleValue();
        try {
            return cur == null ? null : Double.parseDouble(cur.toString());
        } catch (Exception ignored) {
        }
        return null;
    }
}