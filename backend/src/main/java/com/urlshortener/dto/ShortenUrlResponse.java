package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlResponse {
    
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
}
