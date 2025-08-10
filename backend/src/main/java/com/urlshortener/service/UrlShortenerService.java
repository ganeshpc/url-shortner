package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenerService {
    
    private static final int MAX_RETRY_ATTEMPTS = 5;
    
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    
    public UrlShortenerService(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator) {
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }
    
    /**
     * Shorten a URL using production-grade short code generation
     */
    public Url shortenUrl(String originalUrl) {
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        
        String shortCode = generateUniqueShortCode(originalUrl);
        Url url = new Url(originalUrl, shortCode);
        return urlRepository.save(url);
    }
    
    /**
     * Get original URL and increment click count atomically
     */
    public Optional<Url> getOriginalUrl(String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        if (url.isPresent()) {
            // Atomic increment to avoid race conditions
            urlRepository.incrementClickCount(shortCode);
        }
        return url;
    }
    
    /**
     * Generate unique short code using multiple strategies with collision avoidance
     */
    private String generateUniqueShortCode(String originalUrl) {
        String shortCode;
        int attempts = 0;
        
        do {
            attempts++;
            
            // Strategy 1: Hash-based (deterministic, same URL = same code)
            if (attempts == 1) {
                shortCode = shortCodeGenerator.generateHashBased(originalUrl);
            }
            // Strategy 2: Counter-based (guaranteed unique, sequential)
            else if (attempts <= 3) {
                shortCode = shortCodeGenerator.generateCounterBased();
            }
            // Strategy 3: Random (maximum entropy, unpredictable)
            else {
                shortCode = shortCodeGenerator.generateRandom();
            }
            
            if (attempts > MAX_RETRY_ATTEMPTS) {
                throw new RuntimeException("Unable to generate unique short code after " + MAX_RETRY_ATTEMPTS + " attempts");
            }
            
        } while (urlRepository.existsByShortCode(shortCode));
        
        return shortCode;
    }
    
    /**
     * Enhanced URL validation using modern Java URI parsing
     */
    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            java.net.URI uri = java.net.URI.create(url);
            return uri.getScheme() != null && 
                   (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                   uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
