package com.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.config.AppProperties;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @MockBean
    private AppProperties appProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private Url testUrl;
    private static final String TEST_SHORT_CODE = "abc123";
    private static final String TEST_ORIGINAL_URL = "https://example.com";
    private static final String TEST_BASE_URL = "http://localhost:8080";
    private static final String TEST_FRONTEND_URL = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        testUrl = new Url(TEST_ORIGINAL_URL, TEST_SHORT_CODE);
        testUrl.setId(1L);
        testUrl.setCreatedAt(LocalDateTime.now());
        testUrl.setClickCount(5L);

        when(appProperties.getBaseUrl()).thenReturn(TEST_BASE_URL);
        when(appProperties.getFrontendUrl()).thenReturn(TEST_FRONTEND_URL);
    }

    @Test
    void testShortenUrl_Success() throws Exception {
        // Arrange
        ShortenUrlRequest request = new ShortenUrlRequest(TEST_ORIGINAL_URL);
        when(urlShortenerService.shortenUrl(anyString())).thenReturn(testUrl);

        ShortenUrlResponse expectedResponse = new ShortenUrlResponse(
            TEST_SHORT_CODE,
            TEST_BASE_URL + "/" + TEST_SHORT_CODE,
            TEST_ORIGINAL_URL
        );

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortCode").value(TEST_SHORT_CODE))
                .andExpect(jsonPath("$.shortUrl").value(TEST_BASE_URL + "/" + TEST_SHORT_CODE))
                .andExpect(jsonPath("$.originalUrl").value(TEST_ORIGINAL_URL));
    }

    @Test
    void testShortenUrl_InvalidUrl() throws Exception {
        // Arrange
        ShortenUrlRequest request = new ShortenUrlRequest("invalid-url");
        when(urlShortenerService.shortenUrl(anyString()))
            .thenThrow(new IllegalArgumentException("Invalid URL format"));

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShortenUrl_ValidationError_BlankUrl() throws Exception {
        // Arrange
        ShortenUrlRequest request = new ShortenUrlRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRedirectToOriginalUrl_Found() throws Exception {
        // Arrange
        when(urlShortenerService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(Optional.of(testUrl));

        // Act & Assert
        mockMvc.perform(get("/" + TEST_SHORT_CODE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(TEST_ORIGINAL_URL));
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() throws Exception {
        // Arrange
        when(urlShortenerService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/" + TEST_SHORT_CODE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(TEST_FRONTEND_URL + "/not-found"));
    }

    @Test
    void testGetUrlStats_Found() throws Exception {
        // Arrange
        when(urlShortenerService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(Optional.of(testUrl));

        // Act & Assert
        mockMvc.perform(get("/api/stats/" + TEST_SHORT_CODE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalUrl").value(TEST_ORIGINAL_URL))
                .andExpect(jsonPath("$.shortCode").value(TEST_SHORT_CODE))
                .andExpect(jsonPath("$.clickCount").value(5));
    }

    @Test
    void testGetUrlStats_NotFound() throws Exception {
        // Arrange
        when(urlShortenerService.getOriginalUrl(TEST_SHORT_CODE)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/stats/" + TEST_SHORT_CODE))
                .andExpect(status().isNotFound());
    }

    @Test
    void testShortenUrl_InvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShortenUrl_MissingContentType() throws Exception {
        // Arrange
        ShortenUrlRequest request = new ShortenUrlRequest(TEST_ORIGINAL_URL);

        // Act & Assert
        mockMvc.perform(post("/api/shorten")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }
}