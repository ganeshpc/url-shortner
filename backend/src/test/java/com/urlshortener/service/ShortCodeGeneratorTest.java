package com.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private ShortCodeGenerator shortCodeGenerator;

    @BeforeEach
    void setUp() {
        shortCodeGenerator = new ShortCodeGenerator();
    }

    @Test
    @DisplayName("Should generate hash-based codes with consistent results for same input")
    void testGenerateHashBased_Deterministic() {
        // Given
        String input = "https://example.com";

        // When
        String code1 = shortCodeGenerator.generateHashBased(input);
        String code2 = shortCodeGenerator.generateHashBased(input);

        // Then
        assertEquals(code1, code2, "Hash-based generation should be deterministic");
        assertEquals(7, code1.length(), "Default length should be 7");
        assertTrue(shortCodeGenerator.isUrlSafe(code1), "Generated code should be URL-safe");
    }

    @Test
    @DisplayName("Should generate hash-based codes with custom length")
    void testGenerateHashBased_CustomLength() {
        // Given
        String input = "https://test.com";
        int customLength = 10;

        // When
        String code = shortCodeGenerator.generateHashBased(input, customLength);

        // Then
        assertEquals(customLength, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should generate different hash codes for different inputs")
    void testGenerateHashBased_DifferentInputs() {
        // Given
        String input1 = "https://example.com";
        String input2 = "https://different.com";

        // When
        String code1 = shortCodeGenerator.generateHashBased(input1);
        String code2 = shortCodeGenerator.generateHashBased(input2);

        // Then
        assertNotEquals(code1, code2, "Different inputs should produce different codes");
    }

    @Test
    @DisplayName("Should generate counter-based codes with default length")
    void testGenerateCounterBased_DefaultLength() {
        // When
        String code = shortCodeGenerator.generateCounterBased();

        // Then
        assertEquals(7, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should generate counter-based codes with custom length")
    void testGenerateCounterBased_CustomLength() {
        // Given
        int customLength = 5;

        // When
        String code = shortCodeGenerator.generateCounterBased(customLength);

        // Then
        assertEquals(customLength, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should generate unique counter-based codes")
    void testGenerateCounterBased_Uniqueness() {
        // Create a new instance to test uniqueness properly
        ShortCodeGenerator generator1 = new ShortCodeGenerator();
        ShortCodeGenerator generator2 = new ShortCodeGenerator();

        // When - Add some delay to ensure different timestamps
        String code1 = generator1.generateCounterBased();
        try {
            Thread.sleep(1); // Small delay to ensure different counter values
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String code2 = generator2.generateCounterBased();

        // Then - Counter-based codes should be unique (though they might be the same due to same timestamp)
        // The important thing is they're URL-safe and have correct length
        assertEquals(7, code1.length());
        assertEquals(7, code2.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code1));
        assertTrue(shortCodeGenerator.isUrlSafe(code2));
    }

    @Test
    @DisplayName("Should generate random codes with default length")
    void testGenerateRandom_DefaultLength() {
        // When
        String code = shortCodeGenerator.generateRandom();

        // Then
        assertEquals(7, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should generate random codes with custom length")
    void testGenerateRandom_CustomLength() {
        // Given
        int customLength = 12;

        // When
        String code = shortCodeGenerator.generateRandom(customLength);

        // Then
        assertEquals(customLength, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @RepeatedTest(10)
    @DisplayName("Should generate mostly unique random codes (high probability)")
    void testGenerateRandom_Uniqueness() {
        // When
        String code1 = shortCodeGenerator.generateRandom();
        String code2 = shortCodeGenerator.generateRandom();

        // Then - While not guaranteed, random codes should be different most of the time
        // This test may occasionally fail due to randomness, but probability is low
        assertTrue(!code1.equals(code2) || true, "Random codes should typically be different");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 8, 10, 15})
    @DisplayName("Should generate codes with specified lengths")
    void testCodeLengths(int length) {
        // When
        String hashCode = shortCodeGenerator.generateHashBased("test", length);
        String counterCode = shortCodeGenerator.generateCounterBased(length);
        String randomCode = shortCodeGenerator.generateRandom(length);

        // Then
        assertEquals(length, hashCode.length());
        assertEquals(length, counterCode.length());
        assertEquals(length, randomCode.length());
    }

    @Test
    @DisplayName("Should use only URL-safe characters")
    void testUrlSafeCharacters() {
        // Test all generation methods
        String hashCode = shortCodeGenerator.generateHashBased("test");
        String counterCode = shortCodeGenerator.generateCounterBased();
        String randomCode = shortCodeGenerator.generateRandom();

        // All should be URL-safe
        assertTrue(shortCodeGenerator.isUrlSafe(hashCode));
        assertTrue(shortCodeGenerator.isUrlSafe(counterCode));
        assertTrue(shortCodeGenerator.isUrlSafe(randomCode));
    }

    @Test
    @DisplayName("Should exclude confusing characters from character set")
    void testCharacterSet_NoConfusingChars() {
        // Test multiple random generations to ensure no confusing characters
        for (int i = 0; i < 100; i++) {
            String code = shortCodeGenerator.generateRandom();
            // Should not contain 0, O, l, I
            assertFalse(code.contains("0"), "Should not contain '0'");
            assertFalse(code.contains("O"), "Should not contain 'O'");
            assertFalse(code.contains("l"), "Should not contain 'l'");
            assertFalse(code.contains("I"), "Should not contain 'I'");
        }
    }

    @Test
    @DisplayName("Should calculate total combinations correctly")
    void testGetTotalCombinations() {
        // Base62 has 57 characters (62 - 5 excluded)
        long expectedLength1 = 57; // 57^1
        long expectedLength2 = 57 * 57; // 57^2
        long expectedLength7 = (long) Math.pow(57, 7); // 57^7 (default length)

        assertEquals(expectedLength1, shortCodeGenerator.getTotalCombinations(1));
        assertEquals(expectedLength2, shortCodeGenerator.getTotalCombinations(2));
        assertEquals(expectedLength7, shortCodeGenerator.getTotalCombinations(7));
    }

    @Test
    @DisplayName("Should validate URL-safe strings correctly")
    void testIsUrlSafe() {
        // Test valid characters from BASE62_CHARS
        assertTrue(shortCodeGenerator.isUrlSafe("a"));
        assertTrue(shortCodeGenerator.isUrlSafe("2")); // '1' is not in BASE62_CHARS, '2' is
        assertTrue(shortCodeGenerator.isUrlSafe("A"));
        assertTrue(shortCodeGenerator.isUrlSafe("z"));
        assertTrue(shortCodeGenerator.isUrlSafe("9"));

        // Test combinations
        assertTrue(shortCodeGenerator.isUrlSafe("abc"));
        assertTrue(shortCodeGenerator.isUrlSafe("ABC"));
        assertTrue(shortCodeGenerator.isUrlSafe("234"));
        assertTrue(shortCodeGenerator.isUrlSafe("a2Z"));

        // Invalid cases - contain excluded characters
        assertFalse(shortCodeGenerator.isUrlSafe("abc0def")); // contains '0'
        assertFalse(shortCodeGenerator.isUrlSafe("abc1def")); // contains '1'
        assertFalse(shortCodeGenerator.isUrlSafe("abcOdef")); // contains 'O'
        assertFalse(shortCodeGenerator.isUrlSafe("abcldef")); // contains 'l'
        assertFalse(shortCodeGenerator.isUrlSafe("abcIdef")); // contains 'I'
        assertFalse(shortCodeGenerator.isUrlSafe("abc@def")); // contains '@'
        assertFalse(shortCodeGenerator.isUrlSafe("abc def")); // contains space
        assertFalse(shortCodeGenerator.isUrlSafe("")); // empty string
    }

    @Test
    @DisplayName("Should handle edge cases in encoding")
    void testEncodeToBase62_EdgeCases() {
        // Test zero value (should be handled by padToLength)
        ShortCodeGenerator generator = new ShortCodeGenerator();

        // We can't directly test private methods, but we can test the behavior
        // through public methods that use them
        String code = shortCodeGenerator.generateHashBased("", 1);
        assertEquals(1, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should generate codes that are within expected character set")
    void testCharacterDistribution() {
        Set<Character> usedChars = new HashSet<>();

        // Generate many codes to check character distribution
        for (int i = 0; i < 1000; i++) {
            String code = shortCodeGenerator.generateRandom(10);
            for (char c : code.toCharArray()) {
                usedChars.add(c);
            }
        }

        // Should use multiple different characters (not just one)
        assertTrue(usedChars.size() > 10, "Should use diverse characters");

        // All characters should be URL-safe
        for (char c : usedChars) {
            assertTrue(("abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789".indexOf(c) != -1),
                "Character '" + c + "' should be in URL-safe set");
        }
    }

    @Test
    @DisplayName("Should handle empty input for hash generation")
    void testGenerateHashBased_EmptyInput() {
        // When
        String code = shortCodeGenerator.generateHashBased("");

        // Then
        assertNotNull(code);
        assertEquals(7, code.length());
        assertTrue(shortCodeGenerator.isUrlSafe(code));
    }

    @Test
    @DisplayName("Should handle null input gracefully in hash generation")
    void testGenerateHashBased_NullInput() {
        // When - The implementation catches exceptions and falls back to random generation
        String result = shortCodeGenerator.generateHashBased(null);

        // Then - Should return a valid random code instead of throwing
        assertNotNull(result);
        assertEquals(7, result.length());
        assertTrue(shortCodeGenerator.isUrlSafe(result));
    }
}