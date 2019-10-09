package com.revolut.account.service;

public class ClientException extends RuntimeException {

    ClientException(String message) {
        super(message);
    }
}
