package com.revolut.account.request;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UriParserTest {

    private UriParser parser = new UriParser("/v1/accounts");

    @ParameterizedTest
    @ValueSource(strings = {"/v1/accounts/id123/id345", "/v1/accounts/id123/id345/"})
    void parse_twoParts_successful(String uri) {
        String[] parts = parser.parse(uri, 2, 2);

        assertEquals("id123", parts[0]);
        assertEquals("id345", parts[1]);
        assertEquals(2, parts.length);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/v1/accounts/id123", "/v1/accounts/id123/"})
    void parse_onePart_successful(String uri) {
        String[] parts = parser.parse(uri, 1, 2);

        assertEquals("id123", parts[0]);
        assertEquals(1, parts.length);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/v1/accounts", "/v1/accounts/", "/v1/accounts/id123/id345/id567"})
    void parse_wrongNumberOfParts_throwsException(String uri) {
        assertThrows(
                UnsupportedOperationException.class,
                () -> parser.parse(uri, 1, 2)
        );
    }
}