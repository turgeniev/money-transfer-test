package com.revolut.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.account.model.AccountBase;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;

/**
 * Test helper, for deserialization of json responses
 */
class RestAccount extends AccountBase {

    @JsonCreator
    RestAccount(
            @JsonProperty("id") AccountID id,
            @JsonProperty("currencyType") CurrencyType currencyType,
            @JsonProperty("amount") long amount,
            @JsonProperty("state") AccountState state
    ) {
        super(id, currencyType, amount, state);
    }

    long getAmount() {
        return amount;
    }

    AccountState getState() {
        return state;
    }
}