package com.urlshortener.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShortenerService;
import com.urlshortener.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API tests for URL Shortener endpoints using mocked services.
 * Tests controller layer with @WebMvcTest for fast unit testing.
 */
@WebMvcTest
class UrlShortenerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @MockBean
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        // Setup default mock behaviors
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8080");
        when(appProperties.getFrontendUrl()).thenReturn("http://localhost:3000");
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("https://www.google.com");
        Url mockUrl = new Url("https://www.google.com", "abc1234");

        when(urlShortenerService.shortenUrl("https://www.google.com")).thenReturn(mockUrl);

        // When & Then
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortCode").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"));
    }

    @Test
    void testCreateShortUrl_InvalidUrl() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("not-a-valid-url");

        when(urlShortenerService.shortenUrl("not-a-valid-url"))
            .thenThrow(new IllegalArgumentException("Invalid URL format"));

        // When & Then
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAccessShortUrl_ExistingUrl() throws Exception {
        // Given
        Url mockUrl = new Url("https://example.com", "test123");
        when(urlShortenerService.getOriginalUrl("test123")).thenReturn(Optional.of(mockUrl));

        // When & Then
        mockMvc.perform(get("/test123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));
    }

    @Test
    void testAccessShortUrl_NonExistingUrl() throws Exception {
        // Given
        when(urlShortenerService.getOriginalUrl("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:3000/not-found"));
    }

    @Test
    void testGetUrlStats_ExistingUrl() throws Exception {
        // Given
        Url mockUrl = new Url("https://stats-test.com", "stats123");
        mockUrl.setId(1L);
        mockUrl.setCreatedAt(LocalDateTime.now());
        mockUrl.setClickCount(5L);

        when(urlShortenerService.getOriginalUrl("stats123")).thenReturn(Optional.of(mockUrl));

        // When & Then
        mockMvc.perform(get("/api/stats/stats123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalUrl").value("https://stats-test.com"))
                .andExpect(jsonPath("$.shortCode").value("stats123"))
                .andExpect(jsonPath("$.clickCount").value(5));
    }

    @Test
    void testGetUrlStats_NonExistingUrl() throws Exception {
        // Given
        when(urlShortenerService.getOriginalUrl("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/stats/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCorsHeaders() throws Exception {
        // Given
        ShortenUrlRequest request = new ShortenUrlRequest("https://cors-test.com");
        Url mockUrl = new Url("https://cors-test.com", "cors123");

        when(urlShortenerService.shortenUrl("https://cors-test.com")).thenReturn(mockUrl);

        // When & Then
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    // Note: Health endpoint test removed as @WebMvcTest doesn't load actuator endpoints
    // Health endpoint testing should be done in integration tests if needed
}