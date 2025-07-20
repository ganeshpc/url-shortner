package com.urlshortener.controller;

import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UrlController {
    
    private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        try {
            Url url = urlShortenerService.shortenUrl(request.getOriginalUrl());
            ShortenUrlResponse response = new ShortenUrlResponse(
                url.getShortCode(),
                "http://localhost:8080/" + url.getShortCode(),
                url.getOriginalUrl()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlShortenerService.getOriginalUrl(shortCode);
        if (url.isPresent()) {
            return new RedirectView(url.get().getOriginalUrl());
        } else {
            return new RedirectView("http://localhost:3000/not-found");
        }
    }
    
    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<Url> getUrlStats(@PathVariable String shortCode) {
        Optional<Url> url = urlShortenerService.getOriginalUrl(shortCode);
        return url.map(value -> ResponseEntity.ok().body(value))
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
