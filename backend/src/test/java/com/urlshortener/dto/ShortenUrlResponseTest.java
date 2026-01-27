package com.urlshortener.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortenUrlResponseTest {

    @Test
    void testDefaultConstructor() {
        ShortenUrlResponse response = new ShortenUrlResponse();
        assertNull(response.getShortCode());
        assertNull(response.getShortUrl());
        assertNull(response.getOriginalUrl());
    }

    @Test
    void testAllArgsConstructor() {
        String shortCode = "abc123";
        String shortUrl = "http://localhost:8080/abc123";
        String originalUrl = "https://example.com";

        ShortenUrlResponse response = new ShortenUrlResponse(shortCode, shortUrl, originalUrl);

        assertEquals(shortCode, response.getShortCode());
        assertEquals(shortUrl, response.getShortUrl());
        assertEquals(originalUrl, response.getOriginalUrl());
    }

    @Test
    void testSettersAndGetters() {
        ShortenUrlResponse response = new ShortenUrlResponse();

        String shortCode = "def456";
        String shortUrl = "http://localhost:8080/def456";
        String originalUrl = "https://test.com";

        response.setShortCode(shortCode);
        response.setShortUrl(shortUrl);
        response.setOriginalUrl(originalUrl);

        assertEquals(shortCode, response.getShortCode());
        assertEquals(shortUrl, response.getShortUrl());
        assertEquals(originalUrl, response.getOriginalUrl());
    }

    @Test
    void testToString() {
        ShortenUrlResponse response = new ShortenUrlResponse("abc123", "http://localhost:8080/abc123", "https://example.com");
        String toString = response.toString();

        // Lombok @Data generates toString, so we check it contains the expected fields
        assertTrue(toString.contains("abc123"));
        assertTrue(toString.contains("http://localhost:8080/abc123"));
        assertTrue(toString.contains("https://example.com"));
    }

    @Test
    void testEqualsAndHashCode() {
        ShortenUrlResponse response1 = new ShortenUrlResponse("abc123", "http://localhost:8080/abc123", "https://example.com");
        ShortenUrlResponse response2 = new ShortenUrlResponse("abc123", "http://localhost:8080/abc123", "https://example.com");
        ShortenUrlResponse response3 = new ShortenUrlResponse("def456", "http://localhost:8080/def456", "https://test.com");

        // Test equals
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());

        // Test hashCode
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testNullFieldsEquality() {
        ShortenUrlResponse response1 = new ShortenUrlResponse(null, null, null);
        ShortenUrlResponse response2 = new ShortenUrlResponse(null, null, null);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }
}