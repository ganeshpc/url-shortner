package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private UrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        urlShortenerService = new UrlShortenerService(urlRepository, shortCodeGenerator);
    }

    @Test
    @DisplayName("Should successfully shorten a valid URL")
    void testShortenUrl_Success() {
        // Given
        String originalUrl = "https://example.com";
        String shortCode = "abc123";

        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn(shortCode);
        when(urlRepository.existsByShortCode(shortCode)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L); // Simulate JPA setting ID
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(originalUrl);

        // Then
        assertNotNull(result);
        assertEquals(originalUrl, result.getOriginalUrl());
        assertEquals(shortCode, result.getShortCode());
        assertEquals(0L, result.getClickCount());

        verify(shortCodeGenerator).generateHashBased(originalUrl);
        verify(urlRepository).existsByShortCode(shortCode);
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid URL")
    void testShortenUrl_InvalidUrl() {
        // Given
        String invalidUrl = "not-a-url";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl(invalidUrl);
        });

        assertEquals("Invalid URL format", exception.getMessage());
        verifyNoInteractions(shortCodeGenerator, urlRepository);
    }

    @Test
    @DisplayName("Should throw exception for null URL")
    void testShortenUrl_NullUrl() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl(null);
        });

        assertEquals("Invalid URL format", exception.getMessage());
        verifyNoInteractions(shortCodeGenerator, urlRepository);
    }

    @Test
    @DisplayName("Should throw exception for empty URL")
    void testShortenUrl_EmptyUrl() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl("");
        });

        assertEquals("Invalid URL format", exception.getMessage());
        verifyNoInteractions(shortCodeGenerator, urlRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com",
        "https://example.com",
        "https://subdomain.example.com",
        "https://example.com/path",
        "https://example.com/path/to/resource",
        "https://example.com?param=value",
        "https://example.com/path?param=value&other=test"
    })
    @DisplayName("Should accept valid URLs with various formats")
    void testShortenUrl_ValidUrls(String validUrl) {
        // Given
        String shortCode = "test123";

        when(shortCodeGenerator.generateHashBased(validUrl)).thenReturn(shortCode);
        when(urlRepository.existsByShortCode(shortCode)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(validUrl);

        // Then
        assertNotNull(result);
        assertEquals(validUrl, result.getOriginalUrl());
        assertEquals(shortCode, result.getShortCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ftp://example.com",
        "file:///path/to/file",
        "example.com",
        "://example.com",
        "http://",
        "https://",
        "http:///path",
        "invalid://example.com"
    })
    @DisplayName("Should reject invalid URL formats")
    void testShortenUrl_InvalidFormats(String invalidUrl) {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.shortenUrl(invalidUrl);
        });

        assertEquals("Invalid URL format", exception.getMessage());
        verifyNoInteractions(shortCodeGenerator, urlRepository);
    }

    @Test
    @DisplayName("Should handle collision by using counter-based generation")
    void testShortenUrl_Collision_UsesCounter() {
        // Given
        String originalUrl = "https://collision.com";
        String hashCode = "collision";
        String counterCode = "counter1";

        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn(hashCode);
        when(shortCodeGenerator.generateCounterBased()).thenReturn(counterCode);
        when(urlRepository.existsByShortCode(hashCode)).thenReturn(true);
        when(urlRepository.existsByShortCode(counterCode)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(originalUrl);

        // Then
        assertEquals(counterCode, result.getShortCode());
        verify(shortCodeGenerator).generateHashBased(originalUrl);
        verify(shortCodeGenerator).generateCounterBased();
        verify(urlRepository).existsByShortCode(hashCode);
        verify(urlRepository).existsByShortCode(counterCode);
    }

    @Test
    @DisplayName("Should handle multiple collisions by using random generation")
    void testShortenUrl_MultipleCollisions_UsesRandom() {
        // Given
        String originalUrl = "https://multiple-collisions.com";
        String hashCode = "hashcode";
        String counterCode1 = "counter1";
        String counterCode2 = "counter2";
        String randomCode = "random1";

        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn(hashCode);
        when(shortCodeGenerator.generateCounterBased()).thenReturn(counterCode1, counterCode2);
        when(shortCodeGenerator.generateRandom()).thenReturn(randomCode);

        // All codes exist except the random one
        when(urlRepository.existsByShortCode(hashCode)).thenReturn(true);
        when(urlRepository.existsByShortCode(counterCode1)).thenReturn(true);
        when(urlRepository.existsByShortCode(counterCode2)).thenReturn(true);
        when(urlRepository.existsByShortCode(randomCode)).thenReturn(false);

        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(originalUrl);

        // Then
        assertEquals(randomCode, result.getShortCode());
        verify(shortCodeGenerator).generateHashBased(originalUrl);
        verify(shortCodeGenerator, times(2)).generateCounterBased();
        verify(shortCodeGenerator).generateRandom();
    }

    @Test
    @DisplayName("Should throw exception after maximum retry attempts")
    void testShortenUrl_MaxRetriesExceeded() {
        // Given
        String originalUrl = "https://max-retries.com";

        // All generation strategies return codes that already exist
        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn("code1");
        when(shortCodeGenerator.generateCounterBased()).thenReturn("code2", "code3");
        when(shortCodeGenerator.generateRandom()).thenReturn("code4");

        when(urlRepository.existsByShortCode(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            urlShortenerService.shortenUrl(originalUrl);
        });

        assertTrue(exception.getMessage().contains("Unable to generate unique short code"));
        assertTrue(exception.getMessage().contains("5 attempts"));
    }

    @Test
    @DisplayName("Should find URL by short code when exists")
    void testGetOriginalUrl_Found() {
        // Given
        String shortCode = "existing";
        Url url = new Url("https://found.com", shortCode);
        url.setId(1L);

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(url));

        // When
        Optional<Url> result = urlShortenerService.getOriginalUrl(shortCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(url, result.get());
        verify(urlRepository).findByShortCode(shortCode);
        // Note: incrementClickCount is commented out in the service
    }

    @Test
    @DisplayName("Should return empty when short code doesn't exist")
    void testGetOriginalUrl_NotFound() {
        // Given
        String shortCode = "nonexistent";

        when(urlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        Optional<Url> result = urlShortenerService.getOriginalUrl(shortCode);

        // Then
        assertFalse(result.isPresent());
        verify(urlRepository).findByShortCode(shortCode);
    }

    @Test
    @DisplayName("Should handle null short code gracefully")
    void testGetOriginalUrl_NullShortCode() {
        // When
        Optional<Url> result = urlShortenerService.getOriginalUrl(null);

        // Then
        assertFalse(result.isPresent());
        verify(urlRepository).findByShortCode(null);
    }

    @Test
    @DisplayName("Should handle empty short code gracefully")
    void testGetOriginalUrl_EmptyShortCode() {
        // When
        Optional<Url> result = urlShortenerService.getOriginalUrl("");

        // Then
        assertFalse(result.isPresent());
        verify(urlRepository).findByShortCode("");
    }

    // URL Validation Tests
    @Test
    @DisplayName("Should validate HTTP URLs as valid")
    void testIsValidUrl_Http() {
        // Test the private method indirectly through public API
        assertDoesNotThrow(() -> urlShortenerService.shortenUrl("http://example.com"));
    }

    @Test
    @DisplayName("Should validate HTTPS URLs as valid")
    void testIsValidUrl_Https() {
        assertDoesNotThrow(() -> urlShortenerService.shortenUrl("https://example.com"));
    }

    @Test
    @DisplayName("Should reject URLs with invalid schemes")
    void testIsValidUrl_InvalidScheme() {
        assertThrows(IllegalArgumentException.class, () ->
            urlShortenerService.shortenUrl("ftp://example.com"));
    }

    @Test
    @DisplayName("Should reject URLs without schemes")
    void testIsValidUrl_NoScheme() {
        assertThrows(IllegalArgumentException.class, () ->
            urlShortenerService.shortenUrl("example.com"));
    }

    @Test
    @DisplayName("Should reject URLs without hosts")
    void testIsValidUrl_NoHost() {
        assertThrows(IllegalArgumentException.class, () ->
            urlShortenerService.shortenUrl("https://"));
    }

    @Test
    @DisplayName("Should handle malformed URLs gracefully")
    void testIsValidUrl_Malformed() {
        assertThrows(IllegalArgumentException.class, () ->
            urlShortenerService.shortenUrl("://invalid"));
    }

    @Test
    @DisplayName("Should handle whitespace-only URLs")
    void testIsValidUrl_WhitespaceOnly() {
        assertThrows(IllegalArgumentException.class, () ->
            urlShortenerService.shortenUrl("   "));
    }

    @Test
    @DisplayName("Should handle URLs with complex paths and query parameters")
    void testShortenUrl_ComplexUrl() {
        // Given
        String complexUrl = "https://example.com/path/to/resource?param1=value1&param2=value2#fragment";

        when(shortCodeGenerator.generateHashBased(complexUrl)).thenReturn("complex");
        when(urlRepository.existsByShortCode("complex")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(complexUrl);

        // Then
        assertEquals(complexUrl, result.getOriginalUrl());
    }

    @Test
    @DisplayName("Should use counter-based generation on first collision")
    void testCollisionStrategy_Order() {
        // Given
        String originalUrl = "https://strategy-test.com";
        String hashCode = "hash";
        String counterCode = "counter";

        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn(hashCode);
        when(shortCodeGenerator.generateCounterBased()).thenReturn(counterCode);

        when(urlRepository.existsByShortCode(hashCode)).thenReturn(true);
        when(urlRepository.existsByShortCode(counterCode)).thenReturn(false);

        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(originalUrl);

        // Then
        assertEquals(counterCode, result.getShortCode());

        // Verify the order of generation calls
        verify(shortCodeGenerator).generateHashBased(originalUrl);
        verify(shortCodeGenerator).generateCounterBased();
        verify(shortCodeGenerator, never()).generateRandom();
    }

    @Test
    @DisplayName("Should use counter-based generation for collision resolution")
    void testCollisionStrategy_CounterBased() {
        // Given - Hash collision, use counter-based
        String originalUrl = "https://counter-strategy.com";
        String hashCode = "hash";
        String counterCode = "counter";

        when(shortCodeGenerator.generateHashBased(originalUrl)).thenReturn(hashCode);
        when(shortCodeGenerator.generateCounterBased()).thenReturn(counterCode);

        // Hash exists, counter doesn't
        when(urlRepository.existsByShortCode(hashCode)).thenReturn(true);
        when(urlRepository.existsByShortCode(counterCode)).thenReturn(false);

        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> {
            Url url = invocation.getArgument(0);
            url.setId(1L);
            return url;
        });

        // When
        Url result = urlShortenerService.shortenUrl(originalUrl);

        // Then
        assertEquals(counterCode, result.getShortCode());

        // Verify generation strategy order
        verify(shortCodeGenerator).generateHashBased(originalUrl);
        verify(shortCodeGenerator).generateCounterBased();
        verify(shortCodeGenerator, never()).generateRandom();
    }
}