package com.revolut.account.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;

public class AccountResp {

    private final AccountID id;
    private final CurrencyType currencyType;
    private long amount;
    private AccountState state;

    @JsonCreator
    public AccountResp(
            @JsonProperty("id") AccountID id,
            @JsonProperty("currencyType") CurrencyType currencyType,
            @JsonProperty("amount") long amount,
            @JsonProperty("state") AccountState state
    ) {
        this.id = id;
        this.currencyType = currencyType;
        this.amount = amount;
        this.state = state;
    }

    static AccountResp createFrom(Account account) {
        return new AccountResp(
                account.getId(),
                account.getCurrencyType(),
                account.getAmount(),
                account.getState()
        );
    }

    public String getId() {
        return id.toString();
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public long getAmount() {
        return amount;
    }

    public AccountState getState() {
        return state;
    }
}