package com.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Configuration properties for short code generation
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.shortcode")
public class ShortCodeProperties {
    
    /**
     * Length of generated short codes
     */
    @Min(value = 4, message = "Short code length must be at least 4")
    @Max(value = 12, message = "Short code length must not exceed 12")
    private int length = 7;
    
    /**
     * Maximum retry attempts for collision resolution
     */
    @Min(value = 3, message = "Max retry attempts must be at least 3")
    @Max(value = 10, message = "Max retry attempts must not exceed 10")
    private int maxRetryAttempts = 5;
    
    /**
     * Strategy to use for short code generation
     * Options: HASH_BASED, COUNTER_BASED, RANDOM, MIXED (default)
     */
    private GenerationStrategy strategy = GenerationStrategy.MIXED;
    
    /**
     * Character set to use for encoding
     * Options: BASE62, BASE58 (default), ALPHANUMERIC
     */
    private CharacterSet characterSet = CharacterSet.BASE58;
    
    public enum GenerationStrategy {
        HASH_BASED,     // Deterministic based on URL hash
        COUNTER_BASED,  // Sequential with randomness
        RANDOM,         // Pure random generation
        MIXED          // Try hash first, then counter, then random
    }
    
    public enum CharacterSet {
        BASE62("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"),
        BASE58("abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789"), // No 0,O,l,I,1
        ALPHANUMERIC("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        
        private final String characters;
        
        CharacterSet(String characters) {
            this.characters = characters;
        }
        
        public String getCharacters() {
            return characters;
        }
    }
}
