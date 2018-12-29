package com.revolut.account.request;

import com.revolut.account.model.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BodyReaderTest {

    private static BodyReader bodyReader = new BodyReader();

    @Test
    void readCurrencyType_successful() {
        // When
        final CurrencyType usd = bodyReader.readCurrencyType(
                "{\"" + BodyReader.CURRENCY_TYPE + "\":\"USD\"}"
        );
        // Then
        assertEquals(CurrencyType.USD, usd);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"currencyType\":\"EURO \"}", "{\"currencyType\":\"POUNDS\"}"})
    void readCurrencyType_invalidCurrencyType_throwsException(String json) {
        assertThrows(IllegalArgumentException.class, () -> bodyReader.readCurrencyType(json));
    }

    @Test
    void readAmount_successful() {
        // When
        long amount = bodyReader.readAmount("{\"" + BodyReader.AMOUNT + "\":100}");
        // Then
        assertEquals(100, amount);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"amunt\":100}", "{\"amounts\":200}"})
    void readAmount_missingAmountField_failsWithException(String json) {

        InvalidBodyException ex = assertThrows(
                InvalidBodyException.class,
                () -> bodyReader.readAmount(json)
        );
        assertTrue(ex.getMessage().contains(BodyReader.AMOUNT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\":100}", "{\"amount\":}", "{\"\":200}"})
    void readAmount_invalidJson_failsWithException(String invalidJson) {
        assertThrows(InvalidBodyException.class, () -> bodyReader.readAmount(invalidJson));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"amount\":100.4}", "{\"amount\":202,89}", "{\"amount\":\"abc\"}", "{\"amount\":101a}"})
    void readAmount_invalidAmount_throwsNumberFormatException(String json) {
        assertThrows(NumberFormatException.class, () -> bodyReader.readAmount(json));
    }
}