package com.revolut.account.model;

/**
 * Possible account states
 */
public enum AccountState {
    /**
     * All operations are permitted
     */
    OPEN,
    /**
     * No operations are permitted, only get information about account is possible
     */
    CLOSED
}
