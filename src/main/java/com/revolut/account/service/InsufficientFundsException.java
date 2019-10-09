package com.revolut.account.service;

public class InsufficientFundsException extends ClientException {

    InsufficientFundsException(String message) {
        super(message);
    }
}
