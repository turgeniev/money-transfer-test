package com.revolut.account.service;

/**
 * Account service configuration.
 */
public class AccountServiceConfig {

    public static AccountService service() {
        return new SimpleAccountService(
                new CurrencyConverter(), new AccountStorage()
        );
    }
}
