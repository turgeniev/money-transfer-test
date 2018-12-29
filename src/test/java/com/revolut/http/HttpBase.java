package com.revolut.http;

import com.revolut.account.Application;
import com.revolut.account.model.AccountID;
import com.revolut.account.model.CurrencyType;
import com.revolut.account.request.AccountRequestHandlerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base class for http tests, contains common helper functions.
 */
class HttpBase {

    private static ExecutorService executor;

    static URI accountsUri;
    Client client = ClientBuilder.newClient();

    @BeforeAll
    static void startHttpServer() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Application.main(new String[] {});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        waitWhenApplicationStarts();
    }

    @AfterAll
    static void stopHttpServer() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private static void waitWhenApplicationStarts() throws Exception {
        accountsUri = getAccountsUri(null);
        for (int i = 0; i < 20; i++) {
            try {
                accountsUri.toURL().openConnection().connect();
                return;
            } catch (IOException e) {
                System.out.println("Waiting for http server ...");
                Thread.sleep(250);
            }
        }
        throw new RuntimeException("Could not connect to http server.");
    }

    static URI getAccountsUri(String subPath) {
        return URI.create("http://localhost:" + Application.DEFAULT_PORT +
                AccountRequestHandlerConfig.URL_PATH_PREFIX +
                (subPath != null ? "/" + subPath : "")
        );
    }

    RestAccount createAccount(CurrencyType currencyType) {
        return client
                .target(accountsUri)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json("{\"currencyType\":\"" + currencyType + "\"}"))
                .readEntity(RestAccount.class);
    }

    RestAccount getAccount(RestAccount account) {
        return client
                .target(getAccountsUri(account.getId().toString()))
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(RestAccount.class);
    }

    RestAccount topUp(RestAccount account, long amount) {
        return putAmount(account.getId().toString(), amount);
    }

    RestAccount transfer(RestAccount from, RestAccount to, long amount) {
        return putAmount(from.getId() + "/" + to.getId(), amount);
    }

    RestAccount closeAccount(RestAccount account) {
        return client.target(getAccountsUri(account.getId().toString()))
                .request(MediaType.APPLICATION_JSON)
                .delete()
                .readEntity(RestAccount.class);
    }

    private RestAccount putAmount(String subPath, long amount) {
        return client.target(getAccountsUri(subPath))
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("{\"amount\":" + amount + "}"))
                .readEntity(RestAccount.class);
    }

    String transferS(RestAccount from, RestAccount to, long amount) {
        return putAmountS(from.getId() + "/" + to.getId(), amount);
    }

    private String putAmountS(String subPath, long amount) {
        return client.target(getAccountsUri(subPath))
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json("{\"amount\":" + amount + "}"))
                .readEntity(String.class);
    }

}