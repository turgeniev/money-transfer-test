package com.revolut.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class TestOccurrences {

    Occurrences occurrences() {
        return new OccurrencesSimple();
    }

    @Test
    void count_null_returns_emptyMap() {
        Map<Character, Integer> result = occurrences().count(null);
        assertEquals(0, result.size());
    }

    @Test
    void count_empty_returns_emptyMap() {
        Map<Character, Integer> result = occurrences().count("");
        assertEquals(0, result.size());
    }

    @MethodSource("cases")
    @ParameterizedTest
    void count_ok(String s, Map<Character, Integer> expected) {
        // Given
        // When
        Map<Character, Integer> result = occurrences().count(s);
        // Then
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("abbccc", Map.of('a', 1, 'b', 2, 'c', 3)),
                Arguments.of("zcabcacb", Map.of('a', 2, 'b', 2, 'c', 3, 'z', 1))
        );
    }
}