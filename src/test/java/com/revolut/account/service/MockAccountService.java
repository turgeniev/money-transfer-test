package com.revolut.account.service;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;

/**
 * Dummy implementation of {@link AccountService} used for testing.
 * Receives account prototype upon creation and uses it when executing operations.
 * Creates return value by coping account prototype and applying operation on this copy.
 */
public class MockAccountService implements AccountService {

    private Account prototype;

    public MockAccountService(Account prototype) {
        this.prototype = prototype;
    }

    @Override
    public Account create(CurrencyType currencyType) {
        Account account = new Account(prototype.getId(), currencyType, 0, AccountState.OPEN);
        prototype = account;
        return account;
    }

    @Override
    public Account topUp(AccountID accountID, long amount) {
        Account account = new Account(
                accountID, prototype.getCurrencyType(), prototype.getAmount() + amount, prototype.getState()
        );
        prototype = account;
        return account;
    }

    @Override
    public Account getAccount(AccountID accountID) {
        Account account = new Account(
                accountID, prototype.getCurrencyType(), prototype.getAmount(), prototype.getState()
        );
        prototype = account;
        return account;
    }

    @Override
    public Account transfer(AccountID fromID, AccountID toID, long amount) {
        Account account = new Account(
                fromID, prototype.getCurrencyType(), prototype.getAmount() - amount, prototype.getState()
        );
        prototype = account;
        return account;
    }

    @Override
    public Account close(AccountID accountID) {
        return new Account(
                accountID, prototype.getCurrencyType(), prototype.getAmount(), AccountState.CLOSED
        );
    }
}
