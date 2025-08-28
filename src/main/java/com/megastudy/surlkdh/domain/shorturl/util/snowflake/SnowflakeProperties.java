package com.megastudy.surlkdh.domain.shorturl.util.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {
	private String epoch;
	private int datacenterId;
	private int serverId;
}