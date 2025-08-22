package com.megastudy.surlkdh.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	SecurityScheme securityScheme = new SecurityScheme()
		.type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
		.in(SecurityScheme.In.HEADER).name("Authorization");

	SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
			.addSecurityItem(securityRequirement)
			.servers(List.of(
				new Server().url("http://localhost:8080")
			))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("surl-kdh API")
			.description("단축 URL 생성 관련 API")
			.version("1.0.0");
	}
}
