package com.megastudy.surlkdh.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.infrastructure.security.filter.CustomAuthenticationFilter;
import com.megastudy.surlkdh.infrastructure.security.filter.LoginFilter;
import com.megastudy.surlkdh.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.megastudy.surlkdh.infrastructure.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SpringSecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;

	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder enc) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(uds);
		authProvider.setPasswordEncoder(enc);
		return authProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider provider,
		AuthenticationManager authenticationManager, CustomAuthenticationFilter customAuthenticationFilter) throws
		Exception {
		http
			.authenticationProvider(provider)
			.formLogin(Customizer.withDefaults())
			.httpBasic(httpBasic -> httpBasic.disable())
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(customAuthenticationFilter, JwtAuthenticationFilter.class)
			.addFilterAt(new LoginFilter(authenticationManager, objectMapper, jwtTokenProvider),
				UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/v1/auth/signup",
					"/api/v1/auth/login"
				)
				.permitAll()
				.requestMatchers("/api/**").authenticated()
				.anyRequest().permitAll()
			);

		http
			.sessionManagement(session -> session.
				sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	PasswordEncoder passEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.fromHierarchy(
			Role.ADMIN.getKey() + " > " + Role.LEADER.getKey() + "\n" +
				Role.LEADER.getKey() + " > " + Role.EMPLOYEE.getKey() + "\n"
		);
	}
}