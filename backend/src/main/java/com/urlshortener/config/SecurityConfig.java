package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints (using proper CORS instead)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/shorten", "/actuator/health", "/h2-console/**").permitAll()
                .requestMatchers("/", "/{shortCode}").permitAll()
                
                // Secure admin endpoints - require authentication
                .requestMatchers("/actuator/**").authenticated()
                .requestMatchers("/api/admin/**").authenticated()
                
                // All other requests are public for now
                .anyRequest().permitAll()
            )
            
            // Security headers for production
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Allow H2 console
                .contentTypeOptions(contentType -> contentType.disable())
            );
        
        return http.build();
    }
}
