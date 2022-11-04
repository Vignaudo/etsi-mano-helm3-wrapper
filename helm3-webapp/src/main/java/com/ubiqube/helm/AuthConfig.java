package com.ubiqube.helm;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class AuthConfig {
	@SuppressWarnings("static-method")
	@Bean
	SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/error").permitAll()
				.mvcMatchers("/**").access("hasAuthority('SCOPE_helm')")
				.anyRequest().authenticated()
				.and()
				.oauth2ResourceServer()
				.jwt();
		return http.build();
	}
}
