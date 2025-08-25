package com.megastudy.surlkdh.common.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.maxmind.geoip2.DatabaseReader;

@Configuration
public class GeoIPConfig {

	@Value("${geoip.mmdb.path}")
	private Resource mmdbResource;

	@Bean
	public DatabaseReader databaseReader() throws IOException {
		return new DatabaseReader.Builder(mmdbResource.getInputStream()).build();
	}
}