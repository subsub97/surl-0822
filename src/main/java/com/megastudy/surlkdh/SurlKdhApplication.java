package com.megastudy.surlkdh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class SurlKdhApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurlKdhApplication.class, args);
	}

}
