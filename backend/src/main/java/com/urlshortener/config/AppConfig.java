package com.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppConfig(
    String baseUrl,
    String frontendUrl,
    Cors cors
) {
    public record Cors(String allowedOrigins) {}
}
