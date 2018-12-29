package com.revolut.account.service;

import com.revolut.account.IdGen;
import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.CurrencyType;

class SimpleAccountService implements AccountService {

    private CurrencyConverter currencyConverter;
    private AccountStorage storage;

    SimpleAccountService(CurrencyConverter currencyConverter, AccountStorage storage) {
        this.currencyConverter = currencyConverter;
        this.storage = storage;
    }

    @Override
    public Account create(CurrencyType currencyType) {
        if (currencyType == null) {
            throw new IllegalArgumentException("Currency type is null");
        }
        String id = IdGen.newID();
        LockableAccount account = new LockableAccount(new AccountID(id), currencyType);
        storage.save(account);
        return account.getAccount();
    }

    @Override
    public Account topUp(AccountID accountID, long amount) {
        final LockableAccount account = storage.getById(accountID);
        return account.topUp(amount);
    }

    @Override
    public Account getAccount(AccountID accountID) {
        final LockableAccount account = storage.getById(accountID);
        return account.getAccount();
    }

    @Override
    public Account transfer(AccountID fromID, AccountID toID, long amount) {
        final LockableAccount fromAccount = storage.getById(fromID);
        final LockableAccount toAccount = storage.getById(toID);
        long toAmount = currencyConverter.convert(
                fromAccount.getCurrencyType(), toAccount.getCurrencyType(), amount
        );
        return fromAccount.transferTo(toAccount, amount, toAmount);
    }

    @Override
    public Account close(AccountID accountID) {
        final LockableAccount account = storage.getById(accountID);
        return account.close();
    }
}