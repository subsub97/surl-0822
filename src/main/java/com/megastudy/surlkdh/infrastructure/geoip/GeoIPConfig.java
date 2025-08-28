package com.megastudy.surlkdh.infrastructure.geoip;

import com.maxmind.db.Reader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GeoIPConfig {

    @Value("${geoip.mmdb.path}")
    private String mmdbPath;

    @Bean
    public Reader ipinfoMmdbReader() throws IOException {
        if (mmdbPath.startsWith("classpath:")) {

            String p = mmdbPath.replace("classpath:", "");
            InputStream is = getClass().getResourceAsStream("/" + p);

            if (is == null)
                throw new IllegalStateException("MMDB not found: " + p);
            return new Reader(is);
        } else {
            return new Reader(new File(mmdbPath));
        }
    }
}
