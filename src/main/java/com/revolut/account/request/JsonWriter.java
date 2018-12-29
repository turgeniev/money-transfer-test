package com.revolut.account.request;

import com.revolut.account.model.Account;

class JsonWriter {

    private static final String JSON_TEMPLATE_FMT =
            "{\"id\":\"%s\",\"currencyType\":\"%s\",\"amount\":%d,\"state\":\"%s\"}";

    static String toJson(Account account) {
        return String.format(JSON_TEMPLATE_FMT,
                account.getId(), account.getCurrencyType(), account.getAmount(), account.getState());
    }
}