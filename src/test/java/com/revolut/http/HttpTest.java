package com.revolut.http;

import com.revolut.account.model.AccountState;
import com.revolut.account.model.CurrencyType;
import com.revolut.account.resource.AccountResp;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic test of http operations.
 */
class HttpTest extends HttpBase {

    @Test
    void createAccount_successful() {
        // When
        AccountResp account = createAccount(CurrencyType.RUB);
        // Then
        assertNotNull(account.getId());
        assertEquals(CurrencyType.RUB, account.getCurrencyType());
        assertEquals(AccountState.OPEN, account.getState());
    }

    @Test
    void getAccount_successful() {
        // Given
        AccountResp account = createAccount(CurrencyType.RUB);
        // When
        AccountResp found = getAccount(account);
        // Then
        assertEquals(account.getId(), found.getId());
        assertEquals(account.getAmount(), found.getAmount());
        assertEquals(account.getCurrencyType(), found.getCurrencyType());
        assertEquals(account.getState(), found.getState());
    }
    
    @Test
    void topUp_successful() {
        // Given
        AccountResp account = createAccount(CurrencyType.RUB);
        long topUpAmount = 1000;
        // When
        AccountResp result = topUp(account, topUpAmount);
        // Then
        assertEquals(topUpAmount, result.getAmount());
    }
    
    @Test
    void transfer_successful() {
        // Given
        AccountResp from = createAccount(CurrencyType.RUB);
        AccountResp to = createAccount(CurrencyType.RUB);
        topUp(from, 2000);
        // When
        AccountResp fromResult = transfer(from, to, 300);
        // Then
        assertEquals(1700, fromResult.getAmount());
        assertEquals(300, getAccount(to).getAmount());
    }

    @Test
    void closeAccount_successful() {
        // Given
        AccountResp account = createAccount(CurrencyType.RUB);
        // When
        AccountResp result = closeAccount(account);
        // Then
        assertEquals(AccountState.CLOSED, result.getState());
    }

    @Test
    void createAccount_withNotSupporedCurrency_returnsError() {
        // Given
        final String unsupportedCurrency = "UNSUPPORTED_CURRENCY";
        // When
        Response response = client.target(accountsUri)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json("{\"currencyType\":\"" + unsupportedCurrency + "\"}"));
        // Then
        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.readEntity(String.class).contains(unsupportedCurrency));
    }

}