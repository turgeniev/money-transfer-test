package com.revolut.account.model;

/**
 * Immutable Account information.
 */
public class Account extends AccountBase {

    public Account(AccountID id, CurrencyType currencyType, long amount, AccountState state) {
        super(id, currencyType, amount, state);
    }

    public long getAmount() {
        return amount;
    }

    public AccountState getState() {
        return state;
    }
}
