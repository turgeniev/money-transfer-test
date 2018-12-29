package com.revolut.account.model;

import java.util.Objects;

/**
 * Account base class, holds fields common for all accounts.
 */
public abstract class AccountBase {
    private final AccountID id;
    private final CurrencyType currencyType;
    protected long amount;
    protected AccountState state;

    public AccountBase(AccountID id, CurrencyType currencyType) {
        this(id, currencyType, 0, AccountState.OPEN);
    }

    public AccountBase(AccountID id, CurrencyType currencyType, long amount, AccountState state) {
        this.id = id;
        this.currencyType = currencyType;
        this.amount = amount;
        this.state = state;
    }

    public AccountID getId() {
        return id;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountBase)) return false;
        AccountBase that = (AccountBase) o;
        return amount == that.amount &&
                id.equals(that.id) &&
                currencyType == that.currencyType &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyType, amount, state);
    }

    @Override
    public String toString() {
        return "AccountBase{" +
                "id=" + id +
                ", currencyType=" + currencyType +
                ", state=" + state +
                ", amount=" + amount +
                '}';
    }
}
