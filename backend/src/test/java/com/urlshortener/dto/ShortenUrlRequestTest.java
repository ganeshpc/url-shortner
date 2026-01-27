package com.urlshortener.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShortenUrlRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        if (validator != null) {
            // No specific cleanup needed for validator
        }
    }

    @Test
    void testValidUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest("https://example.com");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid URL should have no validation errors");
    }

    @Test
    void testValidHttpUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest("http://example.com");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid HTTP URL should have no validation errors");
    }

    @Test
    void testNullUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest(null);
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Null URL should have validation errors");
        assertEquals(1, violations.size());
        assertEquals("URL cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testEmptyUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest("");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Empty URL should have validation errors");
        assertEquals(2, violations.size()); // Both @NotBlank and @Pattern violations
        // Check that one of the violations is the @NotBlank message
        boolean hasNotBlankMessage = violations.stream()
            .anyMatch(v -> "URL cannot be blank".equals(v.getMessage()));
        assertTrue(hasNotBlankMessage);
    }

    @Test
    void testBlankUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest("   ");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Blank URL should have validation errors");
        assertEquals(2, violations.size()); // Both @NotBlank and @Pattern violations
        // Check that one of the violations is the @NotBlank message
        boolean hasNotBlankMessage = violations.stream()
            .anyMatch(v -> "URL cannot be blank".equals(v.getMessage()));
        assertTrue(hasNotBlankMessage);
    }

    @Test
    void testUrlWithoutProtocol() {
        ShortenUrlRequest request = new ShortenUrlRequest("example.com");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "URL without protocol should have validation errors");
        assertEquals(1, violations.size());
        assertEquals("URL must start with http:// or https://", violations.iterator().next().getMessage());
    }

    @Test
    void testUrlWithInvalidProtocol() {
        ShortenUrlRequest request = new ShortenUrlRequest("ftp://example.com");
        Set<ConstraintViolation<ShortenUrlRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "URL with invalid protocol should have validation errors");
        assertEquals(1, violations.size());
        assertEquals("URL must start with http:// or https://", violations.iterator().next().getMessage());
    }

    @Test
    void testDefaultConstructor() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        assertNull(request.getOriginalUrl());
    }

    @Test
    void testAllArgsConstructor() {
        String url = "https://test.com";
        ShortenUrlRequest request = new ShortenUrlRequest(url);
        assertEquals(url, request.getOriginalUrl());
    }

    @Test
    void testSetterAndGetter() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        String url = "https://setter-test.com";
        request.setOriginalUrl(url);
        assertEquals(url, request.getOriginalUrl());
    }
}