package com.revolut.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.function.Function;

/**
 */
class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Function<ParsedHttpRequest, String> requestHandler;

    HttpChannelInitializer(Function<ParsedHttpRequest, String> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpResponseEncoder())
                .addLast(new HttpChannelInboundHandler(requestHandler));
    }
}
