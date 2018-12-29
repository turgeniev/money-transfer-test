package com.revolut.account.request;

import com.revolut.account.model.Account;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    @Test
    void toJson() {
        // Given
        Account account = new Account(new AccountID("id"), CurrencyType.RUB, 100, AccountState.OPEN);
        // When
        String json = JsonWriter.toJson(account);
        // Then
        assertEquals("{\"id\":\"id\",\"currencyType\":\"RUB\",\"amount\":100,\"state\":\"OPEN\"}", json);
    }
}