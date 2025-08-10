package com.urlshortener.controller;

import com.urlshortener.config.AppProperties;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.model.Url;
import com.urlshortener.service.RateLimiterService;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UrlController {
    
    private final UrlShortenerService urlShortenerService;
    private final RateLimiterService rateLimiterService;
    private final AppProperties appProperties;

    public UrlController(UrlShortenerService urlShortenerService, 
                        @Autowired(required = false) RateLimiterService rateLimiterService,
                        AppProperties appProperties) {
        this.urlShortenerService = urlShortenerService;
        this.rateLimiterService = rateLimiterService;
        this.appProperties = appProperties;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request,
                                        HttpServletRequest httpRequest) {
        // Rate limiting by IP address (if rate limiter is available)
        if (rateLimiterService != null) {
            String clientIp = getClientIpAddress(httpRequest);
            if (!rateLimiterService.isAllowed(clientIp)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Rate limit exceeded. Please try again later.");
            }
        }
        
        try {
            Url url = urlShortenerService.shortenUrl(request.getOriginalUrl());
            ShortenUrlResponse response = new ShortenUrlResponse(
                url.getShortCode(),
                appProperties.getBaseUrl() + "/" + url.getShortCode(),
                url.getOriginalUrl()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid URL provided");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to shorten URL. Please try again.");
        }
    }
    
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlShortenerService.getOriginalUrl(shortCode);
        if (url.isPresent()) {
            return new RedirectView(url.get().getOriginalUrl());
        } else {
            return new RedirectView(appProperties.getFrontendUrl() + "/not-found");
        }
    }
    
    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<Url> getUrlStats(@PathVariable String shortCode) {
        Optional<Url> url = urlShortenerService.getOriginalUrl(shortCode);
        return url.map(value -> ResponseEntity.ok().body(value))
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Extract client IP address considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
