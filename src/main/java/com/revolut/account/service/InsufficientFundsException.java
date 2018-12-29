package com.revolut.account.service;

public class InsufficientFundsException extends RuntimeException {

    InsufficientFundsException(String message) {
        super(message);
    }
}
