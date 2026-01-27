package com.urlshortener.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.dto.ShortenUrlRequest;
import com.urlshortener.dto.ShortenUrlResponse;
import com.urlshortener.model.Url;
import com.urlshortener.service.UrlShortenerService;
import com.urlshortener.config.AppProperties;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Comprehensive integration tests for URL Shortener API.
 * Tests full application behavior including edge cases and error scenarios.
 */
@WebMvcTest
@DisplayName("URL Shortener API Integration Tests")
class UrlShortenerApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlShortenerService urlShortenerService;

    @MockBean
    private AppProperties appProperties;

    @MockBean
    private com.urlshortener.repository.UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        // Setup default mock behaviors
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8080");
        when(appProperties.getFrontendUrl()).thenReturn("http://localhost:3000");
    }

    @Test
    @DisplayName("Should create and retrieve short URL successfully")
    void testFullUrlLifecycle() throws Exception {
        // Step 1: Create short URL
        ShortenUrlRequest request = new ShortenUrlRequest("https://www.example.com/path?param=value");
        String requestJson = objectMapper.writeValueAsString(request);

        MvcResult createResult = mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").exists())
                .andReturn();

        ShortenUrlResponse response = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), ShortenUrlResponse.class);

        // Step 2: Mock the repository for verification
        Url mockSavedUrl = new Url("https://www.example.com/path?param=value", response.getShortCode());
        when(urlRepository.findByShortCode(response.getShortCode())).thenReturn(java.util.Optional.of(mockSavedUrl));

        // Step 3: Access the short URL (redirect)
        mockMvc.perform(get("/" + response.getShortCode()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://www.example.com/path?param=value"));

        // Step 4: Verify the service was called for URL retrieval
        verify(urlShortenerService).getOriginalUrl(response.getShortCode());

        // Step 5: Get URL statistics
        mockMvc.perform(get("/api/stats/" + response.getShortCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl", is("https://www.example.com/path?param=value")))
                .andExpect(jsonPath("$.shortCode", is(response.getShortCode())));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://google.com",
        "https://github.com/user/repo",
        "https://stackoverflow.com/questions/12345",
        "http://localhost:3000/dashboard",
        "https://api.example.com/v1/users/123"
    })
    @DisplayName("Should handle various real-world URLs")
    void testRealWorldUrls(String testUrl) throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest(testUrl);

        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl", is(testUrl)))
                .andExpect(jsonPath("$.shortCode").isString())
                .andExpect(jsonPath("$.shortUrl", startsWith("http://localhost:8080/")));
    }

    @Test
    @DisplayName("Should generate unique short codes for different URLs")
    void testUniqueShortCodes() throws Exception {
        Set<String> generatedCodes = new HashSet<>();

        // Create multiple URLs
        String[] testUrls = {
            "https://site1.com",
            "https://site2.com",
            "https://site3.com",
            "https://different-site.com"
        };

        for (String url : testUrls) {
            ShortenUrlRequest request = new ShortenUrlRequest(url);
            MvcResult result = mockMvc.perform(post("/api/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            ShortenUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShortenUrlResponse.class);

            // Verify uniqueness
            assertFalse(generatedCodes.contains(response.getShortCode()),
                "Generated short code should be unique: " + response.getShortCode());
            generatedCodes.add(response.getShortCode());
        }

        assertEquals(testUrls.length, generatedCodes.size());
    }

    @Test
    @DisplayName("Should handle concurrent requests gracefully")
    void testConcurrentRequests() throws Exception {
        // This test simulates concurrent access (though not truly concurrent in single-threaded test)
        String baseUrl = "https://concurrent-test.com/";
        Set<String> shortCodes = new HashSet<>();

        // Create 10 URLs rapidly
        for (int i = 0; i < 10; i++) {
            ShortenUrlRequest request = new ShortenUrlRequest(baseUrl + i);
            MvcResult result = mockMvc.perform(post("/api/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            ShortenUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShortenUrlResponse.class);
            shortCodes.add(response.getShortCode());
        }

        // Verify all short codes are unique
        assertEquals(10, shortCodes.size());

        // Verify all URLs are accessible
        for (String shortCode : shortCodes) {
            mockMvc.perform(get("/" + shortCode))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Test
    @DisplayName("Should reject malformed JSON requests")
    void testMalformedJsonRequests() throws Exception {
        // Test JSON that cannot be parsed by Jackson
        String[] malformedJson = {
            "{invalid json}",           // Incomplete object
            "{\"originalUrl\":}",       // Missing value after colon
            "{\"originalUrl\" \"https://example.com\"}", // Missing colon
            "[{\"originalUrl\": \"https://example.com\"}]"  // Array instead of object
        };

        for (String json : malformedJson) {
            mockMvc.perform(post("/api/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Should handle various HTTP methods correctly")
    void testHttpMethods() throws Exception {
        // Test POST (should work)
        ShortenUrlRequest request = new ShortenUrlRequest("https://method-test.com");
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Test GET on shorten endpoint (should not work)
        mockMvc.perform(get("/api/shorten"))
                .andExpect(status().isMethodNotAllowed());

        // Test PUT on shorten endpoint (should not work)
        mockMvc.perform(put("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isMethodNotAllowed());

        // Test DELETE on shorten endpoint (should not work)
        mockMvc.perform(delete("/api/shorten"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should handle URLs with valid special characters")
    void testUrlEncoding() throws Exception {
        // Test URLs with valid special characters that pass URI validation
        String[] validUrls = {
            "https://example.com/path%20with%20spaces",  // URL-encoded spaces
            "https://example.com/path?param=value&another=test",
            "https://example.com/path%2Bencoded"  // URL-encoded characters
        };

        for (String url : validUrls) {
            ShortenUrlRequest request = new ShortenUrlRequest(url);
            Url mockUrl = new Url(url, "test" + Math.abs(url.hashCode()));

            when(urlShortenerService.shortenUrl(url)).thenReturn(mockUrl);

            MvcResult result = mockMvc.perform(post("/api/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            ShortenUrlResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShortenUrlResponse.class);

            assertEquals(url, response.getOriginalUrl());
            assertTrue(response.getShortUrl().startsWith("http://localhost:8080/"));
        }
    }

    @Test
    @DisplayName("Should handle very long URLs")
    void testVeryLongUrls() throws Exception {
        // Create a very long URL (2048 characters should be within limits)
        StringBuilder longUrl = new StringBuilder("https://example.com/");
        while (longUrl.length() < 2000) {
            longUrl.append("path/segment/").append(System.currentTimeMillis()).append("/");
        }
        longUrl.append("end");

        ShortenUrlRequest request = new ShortenUrlRequest(longUrl.toString());

        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").isString());
    }

    @Test
    @DisplayName("Should handle rate limiting simulation")
    void testRapidRequests() throws Exception {
        // Simulate rapid-fire requests to test system stability
        String baseUrl = "https://rapid-test.com/";

        for (int i = 0; i < 50; i++) {
            ShortenUrlRequest request = new ShortenUrlRequest(baseUrl + i);
            mockMvc.perform(post("/api/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // Verify the service was called 50 times
        verify(urlShortenerService, times(50)).shortenUrl(anyString());
    }

    @Test
    @DisplayName("Should handle non-existent short codes gracefully")
    void testNonExistentShortCodes() throws Exception {
        // Test various non-existent short codes
        String[] nonExistentCodes = {
            "nonexistent",
            "123456789",
            "invalid-code",
            "a1b2c3d4e5f6g7",
            "short",
            ""
        };

        for (String code : nonExistentCodes) {
            if (!code.isEmpty()) {
                mockMvc.perform(get("/" + code))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("http://localhost:3000/not-found"));

                mockMvc.perform(get("/api/stats/" + code))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Test
    @DisplayName("Should handle CORS preflight requests")
    void testCorsPreflight() throws Exception {
        // Test CORS preflight request
        mockMvc.perform(options("/api/shorten")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }

    @Test
    @DisplayName("Should provide comprehensive error responses")
    void testErrorResponseFormats() throws Exception {
        // Test invalid URL
        ShortenUrlRequest invalidRequest = new ShortenUrlRequest("invalid-url");
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("")); // Should be empty body for validation errors

        // Test missing content type
        mockMvc.perform(post("/api/shorten")
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnsupportedMediaType());

        // Test invalid JSON
        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}