package com.revolut.account.request;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;
import com.revolut.account.service.MockAccountService;
import com.revolut.http.ParsedHttpRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of http request handler.
 */
class AccountRequestHandlerTest {

    private Account prototype = new Account(
            new AccountID("123"), CurrencyType.RUB, 100000, AccountState.OPEN
    );
    private Function<ParsedHttpRequest, String> handler = AccountRequestHandlerConfig.handler(
            new MockAccountService(prototype)
    );

    @ValueSource(strings = {"/v2/accounts", "/v1/payments", "/v1/accountsInfo"})
    @ParameterizedTest
    void apply_prefixNotMatches_returnsEmptyResponse(String uri) {
        // When
        assertThrows(InputMismatchException.class,
                () -> handler.apply(parsedHttpRequest("GET", uri, ""))
        );
    }

    @ValueSource(strings = {"PATCH", "OPTIONS", "HEAD", "TRACE", ""})
    @ParameterizedTest
    void apply_unsupportedMethod_throwsException(String method) {
        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> handler.apply(parsedHttpRequest(method, "/v1/accounts/123", ""))
        );
        assertTrue(ex.getMessage().contains(method));
    }

    @MethodSource("validRequests")
    @ParameterizedTest
    void apply_validRequests_successful(ParsedHttpRequest request, String expectedResponse) {
        // When
        String response = handler.apply(request);
        // Then
        assertEquals(expectedResponse, response);
    }

    static Stream<Arguments> validRequests() {
        return Stream.of(
                Arguments.of(
                        parsedHttpRequest("POST", "/v1/accounts", "{\"currencyType\":\"USD\"}"),
                        "{\"id\":\"123\",\"currencyType\":\"USD\",\"amount\":0,\"state\":\"OPEN\"}"
                ),
                Arguments.of(
                        parsedHttpRequest("PUT", "/v1/accounts/123", "{\"amount\":\"200000\"}"),
                        "{\"id\":\"123\",\"currencyType\":\"RUB\",\"amount\":300000,\"state\":\"OPEN\"}"
                ),
                Arguments.of(
                        parsedHttpRequest("GET", "/v1/accounts/123", ""),
                        "{\"id\":\"123\",\"currencyType\":\"RUB\",\"amount\":100000,\"state\":\"OPEN\"}"
                ),
                Arguments.of(
                        parsedHttpRequest("PUT", "/v1/accounts/234/123", "{\"amount\":\"50000\"}"),
                        "{\"id\":\"234\",\"currencyType\":\"RUB\",\"amount\":50000,\"state\":\"OPEN\"}"
                ),
                Arguments.of(
                        parsedHttpRequest("DELETE", "/v1/accounts/234", ""),
                        "{\"id\":\"234\",\"currencyType\":\"RUB\",\"amount\":100000,\"state\":\"CLOSED\"}"
                )
        );
    }

    @MethodSource("incorrectRequests")
    @ParameterizedTest
    void apply_incorrectRequests_throwsException(ParsedHttpRequest request) {
        assertThrows(
                UnsupportedOperationException.class, () -> handler.apply(request)
        );
    }

    static Stream<ParsedHttpRequest> incorrectRequests() {
        return Stream.of(
                parsedHttpRequest("GET", "/v1/accounts/", ""),
                parsedHttpRequest("GET", "/v1/accounts", ""),
                parsedHttpRequest("GET", "/v1/accounts/123/234", ""),
                parsedHttpRequest("DELETE", "/v1/accounts/", ""),
                parsedHttpRequest("DELETE", "/v1/accounts", ""),
                parsedHttpRequest("DELETE", "/v1/accounts/123/234", ""),
                parsedHttpRequest("PUT", "/v1/accounts/", "{\"amount\":\"50000\"}"),
                parsedHttpRequest("PUT", "/v1/accounts/234/123/567", "{\"amount\":\"50000\"}"),
                parsedHttpRequest("PUT", "/v1/accounts/234/123/567", "{\"amount\":\"50000\"}")
        );
    }

    private static ParsedHttpRequest parsedHttpRequest(String method, String uri, String body) {
        return new ParsedHttpRequest(method, uri, Collections.emptyMap(), body);
    }
}