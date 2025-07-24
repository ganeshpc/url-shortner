package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlRequest {
    
    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String originalUrl;
}
