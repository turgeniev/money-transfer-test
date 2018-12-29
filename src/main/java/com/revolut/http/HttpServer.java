package com.revolut.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.function.Function;

/**
 *
 */
public class HttpServer {

    private final Function<ParsedHttpRequest, String> requestHandler;
    private final int port;

    public HttpServer(Function<ParsedHttpRequest, String> handler, int port) {
        this.requestHandler = handler;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(acceptorGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpChannelInitializer(requestHandler))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            Channel channel = b.bind(port).sync().channel();

            System.out.printf("HTTP server is listening on all IPs, port %1$d, (e.g. http://localhost:%1$d)\n", port);

            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            acceptorGroup.shutdownGracefully();
        }
    }
}