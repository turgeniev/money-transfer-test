package com.revolut.account.service;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountBase;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Mutable thread-safe account.
 */
class LockableAccount extends AccountBase {
    private static final int ATTEMPTS = 10;

    private Lock lock;

    LockableAccount(AccountID id, CurrencyType currencyType) {
        super(id, currencyType);
        lock = new ReentrantLock();
    }

    Account getAccount() {
        return invokeSafely(this::takeAccountSnapshot);
    }

    Account topUp(long amount) {
        assertAmountPositive(amount);
        return invokeSafely(() -> {
                    assertAccountOpen(this);
                    this.amount += amount;
                    return takeAccountSnapshot();
                }
        );
    }

    Account transferTo(LockableAccount toAccount, long fromAmount, long toAmount) {
        assertDifferentAccount(toAccount);
        assertAmountPositive(fromAmount);
        assertAmountPositive(toAmount);

        for (int i = 0; i < ATTEMPTS; i++) {
            if (lock.tryLock()) {
                try {
                    assertAccountOpen(this);
                    if (toAccount.lock.tryLock()) {
                        try {
                            assertAccountOpen(toAccount);

                            // from
                            if (this.amount - fromAmount < 0) {
                                throw new InsufficientFundsException("Cannot withdraw amount: " + fromAmount);
                            }
                            this.amount -= fromAmount;
                            // to
                            toAccount.amount += toAmount;

                            // DONE: return view of account after the transfer
                            return takeAccountSnapshot();
                        } finally {
                            toAccount.lock.unlock();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
            sleep();
        }
        // no attempts left, fail
        throw new RejectedExecutionException("Unable to transfer money in " + ATTEMPTS  + " attempts");
    }

    Account close() {
        return invokeSafely(() -> {
                    if (this.state == AccountState.CLOSED) {
                        throw new IllegalStateException("Account already closed");
                    }
                    if (this.amount != 0) {
                        throw new IllegalStateException("Cannot close account having non-zero amount on it");
                    }
                    this.state = AccountState.CLOSED;
                    return takeAccountSnapshot(); // DONE
                }
        );
    }

    private <T> T invokeSafely(Callable<T> operation) {
        for (int i = 0; i < ATTEMPTS; i++) {
            if (lock.tryLock()) {
                try {
                    return operation.call();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
            sleep();
        }
        // no attempts left, fail
        throw new RejectedExecutionException("Unable to transfer money in " + ATTEMPTS  + " attempts");
    }

    private static void sleep() {
        try {
            TimeUnit.NANOSECONDS.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private Account takeAccountSnapshot() {
        return new Account(getId(), getCurrencyType(), amount, state);
    }

    private void assertDifferentAccount(LockableAccount toAccount) {
        if (getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer money to the same account");
        }
    }

    private static void assertAmountPositive(long amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount should be positive");
        }
    }

    // should be called only while account lock is hold
    private static void assertAccountOpen(LockableAccount account) {
        if (account.state != AccountState.OPEN) {
            throw new IllegalStateException("Account is not open");
        }
    }
}