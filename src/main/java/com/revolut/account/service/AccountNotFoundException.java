package com.revolut.account.service;

import com.revolut.account.model.AccountID;

public class AccountNotFoundException extends ClientException {

    AccountNotFoundException(AccountID accountID) {
        super(String.format("Account '%s' not found", accountID));
    }
}
