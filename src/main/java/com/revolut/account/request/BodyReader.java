package com.revolut.account.request;

import com.revolut.account.model.CurrencyType;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BodyReader {

    static final String AMOUNT = "amount";
    static final String CURRENCY_TYPE = "currencyType";

    /**
     * matches {"word":"value"} or { "word" : value }
     */
    private static final Pattern pattern = Pattern.compile("\"([\\w]+)\"\\s*:\\s*\"?([^\"}]+)\"?");

    BodyReader() {
    }

    CurrencyType readCurrencyType(String requestBody) {
        return readField(requestBody, CURRENCY_TYPE, CurrencyType::valueOf);
    }

    long readAmount(String requestBody) {
        return readField(requestBody, AMOUNT, Long::parseLong);
    }

    private static <T> T readField(String requestBody, String expectedFieldName, Function<String, T> function) {
        final Matcher matcher = pattern.matcher(requestBody);
        if (matcher.find()) {

            String fieldName = matcher.group(1);
            String value = matcher.group(2);

            if (equals(fieldName, expectedFieldName)) {
                return function.apply(value);
            }

            throw new InvalidBodyException("field not found: " + expectedFieldName);
        }
        throw new InvalidBodyException("fields not found in: " + requestBody);
    }

    private static boolean equals(String read, String expected) {
        return expected.equals(read != null ? read.trim() : "");
    }
}
