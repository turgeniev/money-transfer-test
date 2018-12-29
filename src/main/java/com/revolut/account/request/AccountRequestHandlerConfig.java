package com.revolut.account.request;

import com.revolut.account.service.AccountService;
import com.revolut.http.ParsedHttpRequest;

import java.util.Collections;
import java.util.function.Function;

/**
 * Configuration of http request handling.
 */
public class AccountRequestHandlerConfig {

    public static final String URL_PATH_PREFIX = "/v1/accounts";

    public static Function<ParsedHttpRequest, String> handler(AccountService accountService) {
        final AccountRequestHandler accountRequestHandler = new AccountRequestHandler(
                accountService, new UriParser(URL_PATH_PREFIX), new BodyReader()
        );
        return new RequestRouter(
                Collections.singletonList(accountRequestHandler)
        );
    }
}
