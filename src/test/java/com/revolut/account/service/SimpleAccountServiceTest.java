package com.revolut.account.service;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test of AccountService operations.
 */
class SimpleAccountServiceTest {

    private AccountService accountService = AccountServiceConfig.service();
    private Account account;

    @BeforeEach
    void setUp() {
        account = accountService.create(CurrencyType.USD);
    }

    @Test
    void create() {
        CurrencyType rub = CurrencyType.RUB;
        // When
        Account newAccount = accountService.create(rub);
        // Then
        assertNotNull(newAccount.getId());
        assertEquals(rub, newAccount.getCurrencyType());
        assertEquals(0, newAccount.getAmount());
        assertEquals(AccountState.OPEN, newAccount.getState());
    }

    @Test
    void topUp() {
        // When
        Account result = accountService.topUp(account.getId(), 1000);
        // Then
        assertEquals(1000, result.getAmount());
    }

    @Test
    void getAccount() {
        // When
        Account foundAccount = accountService.getAccount(this.account.getId());
        // Then
        assertEquals(account, foundAccount);
    }

    @Test
    void transfer() {
        // Given
        accountService.topUp(account.getId(), 1000);
        Account toAccount = accountService.create(CurrencyType.USD);
        // When
        Account fromAccount = accountService.transfer(account.getId(), toAccount.getId(), 800);
        // Then
        toAccount = accountService.getAccount(toAccount.getId());
        assertEquals(200, fromAccount.getAmount());
        assertEquals(800, toAccount.getAmount());
    }

    @Test
    void close() {
        // When
        Account closedAccount = accountService.close(account.getId());
        // Then
        assertEquals(AccountState.CLOSED, closedAccount.getState());
    }
}