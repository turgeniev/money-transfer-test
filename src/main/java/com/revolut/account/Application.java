package com.revolut.account;

import com.revolut.account.service.AccountService;
import com.revolut.account.service.AccountServiceConfig;
import com.revolut.account.request.AccountRequestHandlerConfig;
import com.revolut.http.HttpServer;

/**
 * Main entry point
 */
public class Application {

    public static final int DEFAULT_PORT = 7777;

    public static void main(String[] args) throws Exception {
        int port = getPort(args, DEFAULT_PORT);

        AccountService accountService = AccountServiceConfig.service();

        new HttpServer(
                AccountRequestHandlerConfig.handler(accountService), port
        ).start();
    }

    private static int getPort(String[] args, int defaultPort) {
        return args.length == 1 ? Integer.parseInt(args[0]) : defaultPort;
    }
}
