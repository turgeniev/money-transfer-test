package com.revolut.http;

import com.revolut.ConcurUtils;
import com.revolut.account.model.CurrencyType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpConcurrencyTest extends HttpBase {

    @Test
    void test() {
        RestAccount a = createAccount(1_000_000);
        RestAccount b = createAccount(2_000_000);
        RestAccount c = createAccount(3_000_000);

        Callable<RestAccount> transfer1000 = () -> transfer(a, b, 100);
        Callable<RestAccount> transfer2000 = () -> transfer(b, c, 200);
        Callable<RestAccount> transfer3000 = () -> transfer(c, a, 300);

        List<Callable<RestAccount>> transfers = ConcurUtils.generateShuffled(
                10_000, transfer1000, transfer2000, transfer3000
        );

        // When
        List<Future<RestAccount>> results = ForkJoinPool.commonPool().invokeAll(transfers);

        // Then
        ConcurUtils.assertNoFailures(results);

        assertEquals(3_000_000, getAccount(a).getAmount());
        assertEquals(1_000_000, getAccount(b).getAmount());
        assertEquals(2_000_000, getAccount(c).getAmount());
    }

    private RestAccount createAccount(int amount) {
        RestAccount acc = createAccount(CurrencyType.RUB);
        topUp(acc, amount);
        return acc;
    }
}
