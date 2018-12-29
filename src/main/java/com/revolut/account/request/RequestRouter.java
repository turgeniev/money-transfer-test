package com.revolut.account.request;

import com.revolut.http.ParsedHttpRequest;

import java.util.InputMismatchException;
import java.util.List;
import java.util.function.Function;

/**
 * Simple request router.
 * Pass incoming request to handlers until some handler process the request.
 * Throws exception if request is left unhandled.
 */
class RequestRouter implements Function<ParsedHttpRequest, String> {

    private List<Function<ParsedHttpRequest, String>> handlers;

    RequestRouter(List<Function<ParsedHttpRequest, String>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public String apply(ParsedHttpRequest request) {
        for (Function<ParsedHttpRequest, String> handler : handlers) {
            final String response = handler.apply(request);
            if (response != null) {
                return response;
            }
        }
        throw new InputMismatchException("No request handler found for uri: " + request.getUri());
    }
}
