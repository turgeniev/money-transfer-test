package com.revolut.account.request;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.CurrencyType;
import com.revolut.account.service.AccountService;
import com.revolut.http.ParsedHttpRequest;

import java.util.function.Function;

/**
 * Simple http requests handler.
 */
class AccountRequestHandler implements Function<ParsedHttpRequest, String> {

    private AccountService accountService;
    private UriParser parser;
    private final BodyReader bodyReader;

    AccountRequestHandler(AccountService accountService, UriParser parser, BodyReader bodyReader) {
        this.accountService = accountService;
        this.parser = parser;
        this.bodyReader = bodyReader;
    }

    @Override
    public String apply(ParsedHttpRequest parsedHttpRequest) {
        if (!parser.prefixMatches(parsedHttpRequest.getUri())) {
            return null;
        }

        Account resultAccount;
        String[] parts;
        switch (parsedHttpRequest.getMethod()) {

            case "POST":

                CurrencyType currencyType = bodyReader.readCurrencyType(parsedHttpRequest.getBody());
                resultAccount = accountService.create(currencyType);
                break;

            case "GET":

                parts = parser.parse(parsedHttpRequest.getUri(), 1, 1);

                resultAccount = accountService.getAccount(new AccountID(parts[0]));
                break;

            case "PUT":

                parts = parser.parse(parsedHttpRequest.getUri(), 1, 2);

                long amount = bodyReader.readAmount(parsedHttpRequest.getBody());
                if (parts.length == 1) {
                    resultAccount = accountService.topUp(new AccountID(parts[0]), amount);
                } else {
                    resultAccount = accountService.transfer(new AccountID(parts[0]), new AccountID(parts[1]), amount);
                }
                break;

            case "DELETE":

                parts = parser.parse(parsedHttpRequest.getUri(), 1, 1);

                resultAccount = accountService.close(new AccountID(parts[0]));
                break;

            default:
                throw new UnsupportedOperationException("Not supported HTTP method: " + parsedHttpRequest.getMethod());
        }

        return JsonWriter.toJson(resultAccount);
    }
}