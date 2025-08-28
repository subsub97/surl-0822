package com.megastudy.surlkdh.common.config;

import com.megastudy.surlkdh.infrastructure.security.filter.CustomAuthenticationFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    SecurityScheme bearerAuthScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER).name(CustomAuthenticationFilter.JWT_HEADER);

    SecurityScheme apiTokenScheme = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name(CustomAuthenticationFilter.API_TOKEN_HEADER);

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuthScheme)
                        .addSecuritySchemes("api-token", apiTokenScheme))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("api-token"))
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
