package com.urlshortener.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodePropertiesTest {

    private ShortCodeProperties properties;
    private Validator validator;

    @BeforeEach
    void setUp() {
        properties = new ShortCodeProperties();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should have default values")
    void testDefaultValues() {
        // Then
        assertEquals(7, properties.getLength());
        assertEquals(5, properties.getMaxRetryAttempts());
        assertEquals(ShortCodeProperties.GenerationStrategy.MIXED, properties.getStrategy());
        assertEquals(ShortCodeProperties.CharacterSet.BASE58, properties.getCharacterSet());
    }

    @Test
    @DisplayName("Should validate length constraints")
    void testLengthValidation() {
        // Valid lengths
        properties.setLength(4);
        assertTrue(validator.validate(properties).isEmpty());

        properties.setLength(7); // default
        assertTrue(validator.validate(properties).isEmpty());

        properties.setLength(12);
        assertTrue(validator.validate(properties).isEmpty());

        // Invalid lengths
        properties.setLength(3); // below minimum
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be at least 4")));

        properties.setLength(13); // above maximum
        violations = validator.validate(properties);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 12")));
    }

    @Test
    @DisplayName("Should validate maxRetryAttempts constraints")
    void testMaxRetryAttemptsValidation() {
        // Valid values
        properties.setMaxRetryAttempts(3);
        assertTrue(validator.validate(properties).isEmpty());

        properties.setMaxRetryAttempts(5); // default
        assertTrue(validator.validate(properties).isEmpty());

        properties.setMaxRetryAttempts(10);
        assertTrue(validator.validate(properties).isEmpty());

        // Invalid values
        properties.setMaxRetryAttempts(2); // below minimum
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must be at least 3")));

        properties.setMaxRetryAttempts(11); // above maximum
        violations = validator.validate(properties);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed 10")));
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        // Given
        int length = 8;
        int maxRetryAttempts = 7;
        ShortCodeProperties.GenerationStrategy strategy = ShortCodeProperties.GenerationStrategy.HASH_BASED;
        ShortCodeProperties.CharacterSet characterSet = ShortCodeProperties.CharacterSet.BASE62;

        // When
        properties.setLength(length);
        properties.setMaxRetryAttempts(maxRetryAttempts);
        properties.setStrategy(strategy);
        properties.setCharacterSet(characterSet);

        // Then
        assertEquals(length, properties.getLength());
        assertEquals(maxRetryAttempts, properties.getMaxRetryAttempts());
        assertEquals(strategy, properties.getStrategy());
        assertEquals(characterSet, properties.getCharacterSet());
    }

    @Test
    @DisplayName("Should test GenerationStrategy enum values")
    void testGenerationStrategyEnum() {
        // Test all enum values
        assertEquals(4, ShortCodeProperties.GenerationStrategy.values().length);

        assertEquals(ShortCodeProperties.GenerationStrategy.HASH_BASED,
                    ShortCodeProperties.GenerationStrategy.valueOf("HASH_BASED"));
        assertEquals(ShortCodeProperties.GenerationStrategy.COUNTER_BASED,
                    ShortCodeProperties.GenerationStrategy.valueOf("COUNTER_BASED"));
        assertEquals(ShortCodeProperties.GenerationStrategy.RANDOM,
                    ShortCodeProperties.GenerationStrategy.valueOf("RANDOM"));
        assertEquals(ShortCodeProperties.GenerationStrategy.MIXED,
                    ShortCodeProperties.GenerationStrategy.valueOf("MIXED"));
    }

    @Test
    @DisplayName("Should test CharacterSet enum values and methods")
    void testCharacterSetEnum() {
        // Test all enum values
        assertEquals(3, ShortCodeProperties.CharacterSet.values().length);

        // Test BASE62
        ShortCodeProperties.CharacterSet base62 = ShortCodeProperties.CharacterSet.BASE62;
        assertEquals("BASE62", base62.name());
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", base62.getCharacters());
        assertEquals(62, base62.getCharacters().length());

        // Test BASE58
        ShortCodeProperties.CharacterSet base58 = ShortCodeProperties.CharacterSet.BASE58;
        assertEquals("BASE58", base58.name());
        assertEquals("abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789", base58.getCharacters());
        assertEquals(57, base58.getCharacters().length());

        // Test ALPHANUMERIC
        ShortCodeProperties.CharacterSet alphanumeric = ShortCodeProperties.CharacterSet.ALPHANUMERIC;
        assertEquals("ALPHANUMERIC", alphanumeric.name());
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", alphanumeric.getCharacters());
        assertEquals(62, alphanumeric.getCharacters().length());
    }

    @Test
    @DisplayName("Should have ConfigurationProperties annotation")
    void testConfigurationPropertiesAnnotation() {
        // Test that the class has the correct annotation
        assertTrue(ShortCodeProperties.class.isAnnotationPresent(org.springframework.boot.context.properties.ConfigurationProperties.class));
        assertTrue(ShortCodeProperties.class.isAnnotationPresent(org.springframework.stereotype.Component.class));

        var annotation = ShortCodeProperties.class.getAnnotation(org.springframework.boot.context.properties.ConfigurationProperties.class);
        assertEquals("app.shortcode", annotation.prefix());
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10, 11, 12})
    @DisplayName("Should accept valid length values")
    void testValidLengths(int length) {
        properties.setLength(length);
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertTrue(violations.isEmpty(), "Length " + length + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 13, 0, -1, 100})
    @DisplayName("Should reject invalid length values")
    void testInvalidLengths(int length) {
        properties.setLength(length);
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertFalse(violations.isEmpty(), "Length " + length + " should be invalid");
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("Should accept valid maxRetryAttempts values")
    void testValidMaxRetryAttempts(int attempts) {
        properties.setMaxRetryAttempts(attempts);
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertTrue(violations.isEmpty(), "Max retry attempts " + attempts + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 11, 0, -1, 100})
    @DisplayName("Should reject invalid maxRetryAttempts values")
    void testInvalidMaxRetryAttempts(int attempts) {
        properties.setMaxRetryAttempts(attempts);
        Set<ConstraintViolation<ShortCodeProperties>> violations = validator.validate(properties);
        assertFalse(violations.isEmpty(), "Max retry attempts " + attempts + " should be invalid");
    }

    @Test
    @DisplayName("Should handle all enum combinations")
    void testEnumCombinations() {
        for (ShortCodeProperties.GenerationStrategy strategy : ShortCodeProperties.GenerationStrategy.values()) {
            for (ShortCodeProperties.CharacterSet charset : ShortCodeProperties.CharacterSet.values()) {
                properties.setStrategy(strategy);
                properties.setCharacterSet(charset);

                assertEquals(strategy, properties.getStrategy());
                assertEquals(charset, properties.getCharacterSet());
            }
        }
    }

    @Test
    @DisplayName("Should maintain immutability of enum character sets")
    void testEnumImmutability() {
        // The character sets should be immutable and not affected by external changes
        String base58Chars = ShortCodeProperties.CharacterSet.BASE58.getCharacters();
        String base62Chars = ShortCodeProperties.CharacterSet.BASE62.getCharacters();

        // Verify they're different
        assertNotEquals(base58Chars, base62Chars);

        // Verify BASE58 excludes certain characters that BASE62 includes
        assertTrue(base62Chars.contains("0"));
        assertTrue(base62Chars.contains("O"));
        assertTrue(base62Chars.contains("l"));
        assertTrue(base62Chars.contains("I"));
        assertTrue(base62Chars.contains("1"));

        assertFalse(base58Chars.contains("0"));
        assertFalse(base58Chars.contains("O"));
        assertFalse(base58Chars.contains("l"));
        assertFalse(base58Chars.contains("I"));
        assertFalse(base58Chars.contains("1"));
    }
}