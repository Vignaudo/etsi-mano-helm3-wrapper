/**
 *     Copyright (C) 2019-2023 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.helm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfig {
	@SuppressWarnings("static-method")
	@Bean
	SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable);
		http.authorizeHttpRequests(autorize -> autorize.requestMatchers("/error", "/actuator/**").permitAll()
				.requestMatchers("/**").hasAnyAuthority("SCOPE_helm")
				.anyRequest().authenticated());
		http.oauth2ResourceServer(x -> x.jwt(Customizer.withDefaults()));
		return http.build();
	}
}
