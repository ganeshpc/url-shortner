package com.urlshortener.repository;

import com.urlshortener.model.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class UrlRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UrlRepository urlRepository;

    private Url createTestUrl(String originalUrl, String shortCode) {
        Url url = new Url(originalUrl, shortCode);
        url.setId(null); // Let JPA generate the ID
        return url;
    }

    @Test
    void testSaveAndFindById() {
        // Given
        Url url = createTestUrl("https://example.com", "abc123");

        // When
        Url savedUrl = urlRepository.save(url);

        // Then
        assertNotNull(savedUrl.getId());
        assertEquals("https://example.com", savedUrl.getOriginalUrl());
        assertEquals("abc123", savedUrl.getShortCode());
        assertNotNull(savedUrl.getCreatedAt());
        assertEquals(0L, savedUrl.getClickCount());

        // Verify it can be retrieved
        Optional<Url> foundUrl = urlRepository.findById(savedUrl.getId());
        assertTrue(foundUrl.isPresent());
        assertEquals(savedUrl.getId(), foundUrl.get().getId());
    }

    @Test
    void testFindByShortCode_Found() {
        // Given
        Url url = createTestUrl("https://test.com", "test123");
        Url savedUrl = entityManager.persistAndFlush(url);

        // When
        Optional<Url> foundUrl = urlRepository.findByShortCode("test123");

        // Then
        assertTrue(foundUrl.isPresent());
        assertEquals(savedUrl.getId(), foundUrl.get().getId());
        assertEquals("https://test.com", foundUrl.get().getOriginalUrl());
        assertEquals("test123", foundUrl.get().getShortCode());
    }

    @Test
    void testFindByShortCode_NotFound() {
        // When
        Optional<Url> foundUrl = urlRepository.findByShortCode("nonexistent");

        // Then
        assertFalse(foundUrl.isPresent());
    }

    @Test
    void testExistsByShortCode_True() {
        // Given
        Url url = createTestUrl("https://exists.com", "exists");
        entityManager.persistAndFlush(url);

        // When & Then
        assertTrue(urlRepository.existsByShortCode("exists"));
    }

    @Test
    void testExistsByShortCode_False() {
        // When & Then
        assertFalse(urlRepository.existsByShortCode("notexists"));
    }

    @Test
    void testFindAll() {
        // Given
        Url url1 = createTestUrl("https://url1.com", "url1");
        Url url2 = createTestUrl("https://url2.com", "url2");
        entityManager.persistAndFlush(url1);
        entityManager.persistAndFlush(url2);

        // When
        List<Url> allUrls = urlRepository.findAll();

        // Then
        assertTrue(allUrls.size() >= 2);
        assertTrue(allUrls.stream().anyMatch(url -> "url1".equals(url.getShortCode())));
        assertTrue(allUrls.stream().anyMatch(url -> "url2".equals(url.getShortCode())));
    }

    @Test
    void testIncrementClickCount_Success() {
        // Given
        Url url = createTestUrl("https://click.com", "click");
        url.setClickCount(5L);
        Url savedUrl = entityManager.persistAndFlush(url);

        // When
        int updatedRows = urlRepository.incrementClickCount("click");

        // Then
        assertEquals(1, updatedRows);
        entityManager.refresh(savedUrl); // Refresh to get updated values
        assertEquals(6L, savedUrl.getClickCount());
    }

    @Test
    void testIncrementClickCount_NoMatchingRecord() {
        // When
        int updatedRows = urlRepository.incrementClickCount("nonexistent");

        // Then
        assertEquals(0, updatedRows);
    }

    @Test
    void testDeleteById() {
        // Given
        Url url = createTestUrl("https://delete.com", "delete");
        Url savedUrl = entityManager.persistAndFlush(url);
        Long id = savedUrl.getId();

        // Verify it exists
        assertTrue(urlRepository.existsById(id));

        // When
        urlRepository.deleteById(id);

        // Then
        assertFalse(urlRepository.existsById(id));
    }

    @Test
    void testUniqueConstraintOnShortCode() {
        // Given
        Url url1 = createTestUrl("https://unique1.com", "unique");
        Url url2 = createTestUrl("https://unique2.com", "unique");

        // Save first URL
        urlRepository.save(url1);

        // When & Then - Attempting to save second URL with same short code should fail
        assertThrows(Exception.class, () -> {
            urlRepository.save(url2);
            entityManager.flush(); // Force flush to trigger constraint violation
        });
    }

    @Test
    void testUrlFieldsPersistence() {
        // Given
        LocalDateTime customTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        Url url = new Url("https://fields.com", "fields");
        url.setCreatedAt(customTime);
        url.setClickCount(42L);

        // When
        Url savedUrl = urlRepository.save(url);

        // Then
        assertEquals("https://fields.com", savedUrl.getOriginalUrl());
        assertEquals("fields", savedUrl.getShortCode());
        assertEquals(customTime, savedUrl.getCreatedAt());
        assertEquals(42L, savedUrl.getClickCount());
    }

    @Test
    void testCount() {
        // Given
        long initialCount = urlRepository.count();

        Url url1 = createTestUrl("https://count1.com", "count1");
        Url url2 = createTestUrl("https://count2.com", "count2");
        urlRepository.save(url1);
        urlRepository.save(url2);

        // When
        long finalCount = urlRepository.count();

        // Then
        assertEquals(initialCount + 2, finalCount);
    }
}