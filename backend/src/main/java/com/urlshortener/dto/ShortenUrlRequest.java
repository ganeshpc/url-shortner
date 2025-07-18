package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ShortenUrlRequest {
    
    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String originalUrl;
    
    public ShortenUrlRequest() {}
    
    public ShortenUrlRequest(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
