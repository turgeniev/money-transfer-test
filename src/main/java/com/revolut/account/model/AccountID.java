package com.revolut.account.model;

import java.util.Objects;

/**
 * ID of account.
 */
public class AccountID implements Comparable<AccountID> {
    private final String id;

    public AccountID(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(AccountID o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountID accountID = (AccountID) o;
        return id.equals(accountID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
