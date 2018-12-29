package com.revolut.account.request;

/**
 * Exception is thrown when {@link BodyReader} cannot parse http body.
 */
public class InvalidBodyException extends RuntimeException {
    InvalidBodyException(String message) {
        super(message);
    }
}
