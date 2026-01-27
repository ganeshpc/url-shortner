package com.urlshortener.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlTest {

    private Url url;
    private static final String TEST_ORIGINAL_URL = "https://example.com";
    private static final String TEST_SHORT_CODE = "abc123";

    @BeforeEach
    void setUp() {
        url = new Url(TEST_ORIGINAL_URL, TEST_SHORT_CODE);
    }

    @Test
    void testConstructorWithParameters() {
        assertNotNull(url);
        assertEquals(TEST_ORIGINAL_URL, url.getOriginalUrl());
        assertEquals(TEST_SHORT_CODE, url.getShortCode());
        assertNotNull(url.getCreatedAt());
        assertEquals(0L, url.getClickCount());
    }

    @Test
    void testDefaultConstructor() {
        Url defaultUrl = new Url();
        assertNull(defaultUrl.getId());
        assertNull(defaultUrl.getOriginalUrl());
        assertNull(defaultUrl.getShortCode());
        assertNull(defaultUrl.getCreatedAt());
        assertEquals(0L, defaultUrl.getClickCount()); // Lombok initializes this to 0L
    }

    @Test
    void testOnCreateSetsCreatedAtWhenNull() {
        Url urlWithNullCreatedAt = new Url();
        urlWithNullCreatedAt.setOriginalUrl(TEST_ORIGINAL_URL);
        urlWithNullCreatedAt.setShortCode(TEST_SHORT_CODE);
        urlWithNullCreatedAt.setCreatedAt(null);
        urlWithNullCreatedAt.setClickCount(null);

        urlWithNullCreatedAt.onCreate();

        assertNotNull(urlWithNullCreatedAt.getCreatedAt());
        assertEquals(0L, urlWithNullCreatedAt.getClickCount());
    }

    @Test
    void testOnCreateDoesNotOverrideExistingValues() {
        LocalDateTime existingTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        Long existingClickCount = 5L;

        url.setCreatedAt(existingTime);
        url.setClickCount(existingClickCount);

        url.onCreate();

        assertEquals(existingTime, url.getCreatedAt());
        assertEquals(existingClickCount, url.getClickCount());
    }

    @Test
    void testIncrementClickCount() {
        assertEquals(0L, url.getClickCount());

        url.incrementClickCount();
        assertEquals(1L, url.getClickCount());

        url.incrementClickCount();
        assertEquals(2L, url.getClickCount());
    }

    @Test
    void testSettersAndGetters() {
        Long id = 1L;
        LocalDateTime createdAt = LocalDateTime.now();
        Long clickCount = 10L;

        url.setId(id);
        url.setCreatedAt(createdAt);
        url.setClickCount(clickCount);

        assertEquals(id, url.getId());
        assertEquals(createdAt, url.getCreatedAt());
        assertEquals(clickCount, url.getClickCount());
    }

    @Test
    void testClickCountDefaultValue() {
        Url newUrl = new Url();
        // Click count is initialized to 0L by field initialization
        assertEquals(0L, newUrl.getClickCount());

        // After onCreate, it should still be 0 (since it's not null)
        newUrl.onCreate();
        assertEquals(0L, newUrl.getClickCount());
    }

    @Test
    void testEntityAnnotations() {
        // Test that the entity has proper JPA annotations
        // This is more of a compile-time check, but we can verify the class has the right structure
        assertTrue(Url.class.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(Url.class.isAnnotationPresent(jakarta.persistence.Table.class));

        var tableAnnotation = Url.class.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("urls", tableAnnotation.name());
    }
}