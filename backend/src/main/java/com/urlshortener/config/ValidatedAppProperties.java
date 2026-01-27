package com.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app")
@Validated
public class ValidatedAppProperties {
    
    @NotBlank(message = "Base URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "Base URL must start with http:// or https://")
    private String baseUrl;
    
    @NotBlank(message = "Frontend URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "Frontend URL must start with http:// or https://")
    private String frontendUrl;
    
    private Cors cors = new Cors();
    
    @Data
    public static class Cors {
        @NotBlank(message = "Allowed origins cannot be blank")
        private String allowedOrigins;
    }
}
