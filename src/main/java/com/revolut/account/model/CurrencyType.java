package com.revolut.account.model;

/**
 * Supported account currency types.
 */
public enum CurrencyType {
    RUB(2),
    USD(2),
    EURO(2);

    // number of digits after the decimal point
    private double pecision;

    CurrencyType(int pecision) {
        this.pecision = pecision;
    }

    public String asString(long amount) {
        return String.format("%." + pecision + "f", amount/pecision);
    }
}
