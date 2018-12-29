package com.revolut.account.service;

import com.revolut.ConcurUtils;
import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.revolut.account.model.CurrencyType.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LockableAccountConcurrencyTest {

    @Test
    void topUp() {
        // Given
        LockableAccount account = new LockableAccount(new AccountID("123"), USD);
        Callable<Account> topUp1000 = () -> account.topUp(1000);
        Callable<Account> topUp200 = () -> account.topUp(200);

        List<Callable<Account>> topUpOperations = IntStream.range(0, 100)
                .mapToObj(n -> n%2 == 0 ? topUp200 : topUp1000)
                .collect(Collectors.toList());

        // When
        ForkJoinPool.commonPool().invokeAll(topUpOperations);

        // Then
        assertEquals(50*1000 + 50*200, account.getAccount().getAmount());
    }

    @Test
    void transfer_AB_BA_successful() {
        // Given
        LockableAccount a = newAccount("123", 1_000_000);
        LockableAccount b = newAccount("345", 2_000_000);

        Callable<Account> transfer1000 = () -> a.transferTo(b,1_000,1_000);
        Callable<Account> transfer2000 = () -> b.transferTo(a,2_000,2_000);

        List<Callable<Account>> transfers = ConcurUtils.generateShuffled(
                1_000, transfer1000, transfer2000
        );

        // When
        List<Future<Account>> results = ForkJoinPool.commonPool().invokeAll(transfers);

        // Then
        ConcurUtils.assertNoFailures(results);
        assertEquals(2_000_000, a.getAccount().getAmount());
        assertEquals(1_000_000, b.getAccount().getAmount());
    }

    @Test
    void transfer_AB_BC_CA_successful() {
        // Given
        LockableAccount a = newAccount("123", 1_000_000);
        LockableAccount b = newAccount("345", 2_000_000);
        LockableAccount c = newAccount("678", 3_000_000);

        Callable<Account> transfer1000 = () -> a.transferTo(b,1_000,1_000);
        Callable<Account> transfer2000 = () -> b.transferTo(c,2_000,2_000);
        Callable<Account> transfer3000 = () -> c.transferTo(a,3_000,3_000);

        List<Callable<Account>> transfers = ConcurUtils.generateShuffled(
                1_000, transfer1000, transfer2000, transfer3000
        );

        // When
        List<Future<Account>> results = ForkJoinPool.commonPool().invokeAll(transfers);

        // Then
        ConcurUtils.assertNoFailures(results);
        assertEquals(3_000_000, a.getAccount().getAmount());
        assertEquals(1_000_000, b.getAccount().getAmount());
        assertEquals(2_000_000, c.getAccount().getAmount());
    }

    private static LockableAccount newAccount(String id, int amount) {
        LockableAccount account = new LockableAccount(new AccountID(id), USD);
        account.topUp(amount);
        return account;
    }

}