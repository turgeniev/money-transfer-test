package com.revolut.account.service;

import com.revolut.account.model.AccountID;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

class AccountStorage {

    private ConcurrentHashMap<AccountID, LockableAccount> map = new ConcurrentHashMap<>();

    LockableAccount getById(AccountID id) {
        assertNotNull(id);

        final LockableAccount account = map.get(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    void save(LockableAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account is null");
        }
        assertNotNull(account.getId());

        map.put(account.getId(), account);
    }

    private static void assertNotNull(AccountID id) {
        if (id == null) {
            throw new IllegalArgumentException("Account ID is null");
        }
    }
}
