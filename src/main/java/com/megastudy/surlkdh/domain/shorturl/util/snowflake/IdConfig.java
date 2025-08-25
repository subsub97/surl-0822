package com.megastudy.surlkdh.domain.shorturl.util.snowflake;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class IdConfig {
}