package com.revolut.account.service;

import com.revolut.account.model.CurrencyType;

class CurrencyConverter {

    CurrencyConverter() {
    }

    long convert(CurrencyType fromCurrency, CurrencyType toCurrency, long amount) {
        if (fromCurrency != toCurrency) {
            throw new UnsupportedOperationException(
                    "Conversion between different currencies not implemented"
            );
        }
        return amount;
    }
}
