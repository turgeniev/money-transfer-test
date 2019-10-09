package com.revolut.account.resource;

import com.revolut.account.model.CurrencyType;

public class CreateAccountReq {
    private CurrencyType currencyType;

    public CurrencyType getCurrencyType() {
        return currencyType;
    }
}
