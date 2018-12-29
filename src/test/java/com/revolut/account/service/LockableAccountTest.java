package com.revolut.account.service;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.revolut.account.model.CurrencyType.EURO;
import static com.revolut.account.model.CurrencyType.RUB;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of account contracts.
 */
class LockableAccountTest {

    private LockableAccount account = new LockableAccount(new AccountID("123"), EURO);

    @Test
    void getAccount_returnsSnapshot() {
        final AccountID id = new AccountID("345");
        final LockableAccount lockableAcc = new LockableAccount(id, RUB);

        assertEquals(id, lockableAcc.getId());
        assertEquals(RUB, lockableAcc.getCurrencyType());

        assertEquals(lockableAcc, lockableAcc.getAccount());
    }

    @Test
    void getAccount_returnsImmutableSnapshot() {
        // Given
        Account snapshot = account.getAccount();
        // When
        account.topUp(1000);
        Account otherSnapshot = account.getAccount();
        // Then
        assertEquals(0, snapshot.getAmount());
        assertEquals(1000, otherSnapshot.getAmount());
    }

    @ValueSource(longs = {1, 100, Long.MAX_VALUE})
    @ParameterizedTest
    void topUp_ofPositiveAmount_successful(long amount) {
        account.topUp(amount);

        Account account = this.account.getAccount();
        assertEquals(amount, account.getAmount());
    }

    @ValueSource(longs = {0, -1, -100, Long.MIN_VALUE})
    @ParameterizedTest
    void topUp_ofNotPositiveAmount_throwsException(long amount) {
        assertThrows(IllegalArgumentException.class, () -> account.topUp(amount));
    }

    @Test
    void topUp_ofClosedAccount_throwsException() {
        // When
        account.close();
        // Then
        assertThrows(IllegalStateException.class, () -> account.topUp(100));
    }

    @Test
    void transferTo_ofPositiveAmount_successful() {
        // Given
        account.topUp(1000);
        LockableAccount otherAccount = new LockableAccount(new AccountID("345"), CurrencyType.RUB);
        // When
        account.transferTo(otherAccount, 700, 50000);
        // Then
        assertEquals(300, account.getAccount().getAmount());
        assertEquals(50000, otherAccount.getAccount().getAmount());
    }

    @Test
    void transferTo_sameAccount_throwsException() {
        LockableAccount sameAccount = new LockableAccount(account.getId(), account.getCurrencyType());
        assertThrows(
                IllegalArgumentException.class,
                () -> account.transferTo(sameAccount, 100, 100)
        );
    }

    @MethodSource("nonPositiveTransfer")
    @ParameterizedTest
    void transferTo_nonPositiveAmount_throwsException(LockableAccount toAccount, long from, long to) {
        assertThrows(
                IllegalArgumentException.class,
                () -> account.transferTo(toAccount, from, to)
        );
    }

    static Stream<Arguments> nonPositiveTransfer() {
        final LockableAccount toAccount = createAnotherAccount();
        return Stream.of(
                Arguments.of(toAccount, 0L, 100L),
                Arguments.of(toAccount, 100L, 0L),
                Arguments.of(toAccount, -100L, 100L),
                Arguments.of(toAccount, 100L, -100L)
        );
    }

    @Test
    void transferTo_whenToAccountIsNotOpen_throwsException() {
        final LockableAccount toAccount = createAnotherAccount();
        toAccount.close();
        assertThrows(
                IllegalStateException.class,
                () -> account.transferTo(toAccount, 100, 100)
        );
    }

    @Test
    void transferTo_whenFromAccountIsNotOpen_throwsException() {
        final LockableAccount toAccount = createAnotherAccount();
        account.close();
        assertThrows(
                IllegalStateException.class,
                () -> account.transferTo(toAccount, 100, 100)
        );
    }

    @Test
    void transferTo_whenInsufficientFunds_throwsException() {
        final LockableAccount toAccount = createAnotherAccount();
        assertThrows(
                InsufficientFundsException.class,
                () -> account.transferTo(toAccount, 100, 100)
        );
    }

    @Test
    void close_successful() {
        account.close();
        assertEquals(AccountState.CLOSED, account.getAccount().getState());
    }

    @Test
    void close_whenAmountIsNotZero_throwsException() {
        account.topUp(100);
        assertThrows(IllegalStateException.class, account::close);
    }

    @MethodSource("operations")
    @ParameterizedTest
    void operation_onClosedAccount_throwsException(Executable operation) {
        assertThrows(IllegalStateException.class, operation);
    }

    static Stream<Executable> operations() {
        final LockableAccount closedAccount = createAnotherAccount();
        closedAccount.close();
        return Stream.of(
                closedAccount::close,
                () -> closedAccount.topUp(100),
                () -> closedAccount.transferTo(new LockableAccount(new AccountID("567"), EURO), 100, 100)
        );
    }

    private static LockableAccount createAnotherAccount() {
        return new LockableAccount(new AccountID("345"), EURO);
    }
}