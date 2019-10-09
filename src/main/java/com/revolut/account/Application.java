package com.revolut.account;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Application {
    public static final int DEFAULT_PORT = 7777;

    public static void main(String[] args) throws Exception {
        int port = getPort(args, DEFAULT_PORT);

        URI baseUri = UriBuilder.fromUri("http://localhost").port(port).build();
        ResourceConfig config = new JerseyConfig();

        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
        stopJettyOnShutdown(server);
        server.start();
        server.join();
    }

    private static int getPort(String[] args, int defaultPort) {
        return args.length == 1 ? Integer.parseInt(args[0]) : defaultPort;
    }

    private static void stopJettyOnShutdown(Server server) {
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (server.isRunning()) {
                        server.stop();
                    }
                    mainThread.join();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
