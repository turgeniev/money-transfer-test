package com.revolut.account.service;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.CurrencyType;

/**
 * Account management API.
 * <p>
 * Account can be in one of two states: {@code OPEN} or {@code CLOSED}.
 * Initially account is open and permits top up and transfer money operations.
 * Account can be closed if it has {@code 0} money on it
 * and after that no operations are possible on this account except getting info.
 * <p>
 * Amounts of money are represented as {@code long} in terms of minimal units,
 * in kopeks for rubles and in cents for dollars and euros
 * (to avoid rounding errors of floating point numbers).
 * For example: 100 euro and 52 cents = 10052.
 */
public interface AccountService {

    /**
     * Creates new account.
     *
     * @param currencyType currency of created account
     * @return created account
     */
    Account create(CurrencyType currencyType);

    /**
     * Adds money to account.
     * Account must be in {@code OPEN} state.
     * Amount must be positive.
     *
     * @param accountID
     * @param amount amount of money in kopeks, cents, etc. to add to given account
     * @return information about account after the operation
     */
    Account topUp(AccountID accountID, long amount);

    /**
     * Get information about account.
     *
     * @param accountID account id
     * @return information about account
     */
    Account getAccount(AccountID accountID);

    /**
     * Transfers money from one account to another.
     * Both accounts (sender and receiver) must be in {@code OPEN} state.
     * Amount must be positive.
     * Sender's account must have enough money otherwise {@link InsufficientFundsException} will be thrown.
     *
     * @param fromID account id of sender
     * @param toID account id of receiver
     * @param amount amount of money to transfer in kopeks, cents, etc.
     * @return information about sender's account after the operation
     */
    Account transfer(AccountID fromID, AccountID toID, long amount);

    /**
     * Closes an account.
     * Account must be in {@code OPEN} state and have 0 amount on it.
     *
     * @param accountID account id
     * @return information about account after the operation
     */
    Account close(AccountID accountID);
}
