package com.book_app_apis.application.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebMvc
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String[] permitAllApis = {
            "/api/categories/**",
            "/api/auth/**",
            "/api/user/avartar/{avartarName}",
            "/api/account/**",
            "/api/product/**",
            "/api/payment/**",
    };

    private static final String[] apiDoc = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] anyAuthorityUserApis = {"/api/user/**"};

    private static final String[] anyAuthorityAdminApis = {"/api/admin/**"};

    private static final String[] corsReact = {
            "http://localhost:3000",
    };

    private static final String USER = "ROLE_USER";
    private static final String ADMIN = "ROLE_ADMIN";
    private static final String SHIPPER = "ROLE_SHIPPER";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(List.of(corsReact)); // Thay đổi thành địa chỉ của ứng dụng frontend
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                        config.setAllowedHeaders(List.of("*"));
                        return config;
                    };
                    corsConfigurer.configurationSource(source);
                })
                .authorizeHttpRequests(req -> req
                        .requestMatchers(permitAllApis).permitAll()
                        .requestMatchers(apiDoc).permitAll()
                        .requestMatchers(anyAuthorityUserApis).hasAnyAuthority(USER)
                        .requestMatchers(anyAuthorityAdminApis).hasAnyAuthority(ADMIN)
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
