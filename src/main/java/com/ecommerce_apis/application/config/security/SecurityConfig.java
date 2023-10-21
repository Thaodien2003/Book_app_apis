package com.ecommerce_apis.application.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private AuthenticationProvider authenticationProvider;

	@Autowired
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationProvider = authenticationProvider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
		.cors(Customizer.withDefaults())
		.authorizeHttpRequests(req -> req
				.requestMatchers(
						"/api/categories/**",
						"/api/auth/**",
						"/api/user/avartar/{avartarName}",
						"/api/account/**",
						"/api/product/**").permitAll()
				.requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN")
				.requestMatchers("/api/user/**").hasAnyAuthority("ROLE_USER")
				.anyRequest()
				.authenticated())
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authenticationProvider(authenticationProvider)
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		//				.logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
		//						.addLogoutHandler(logoutHandler)
		//						.logoutSuccessHandler(
		//								(request, response, authentication) -> SecurityContextHolder.clearContext()));

		return http.build();
	}

}
