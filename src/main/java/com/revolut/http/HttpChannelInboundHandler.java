package com.revolut.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Handler or HTTP messages.
 * Single instance is created per each channel so there are no race conditions.
 * Based on https://netty.io/4.1/xref/io/netty/example/http/snoop/HttpSnoopServerHandler.html
 */
class HttpChannelInboundHandler extends SimpleChannelInboundHandler<Object> {

    private Function<ParsedHttpRequest, String> requestHandler;
    private HttpRequest request;
    private ParsedHttpRequest parsedReq;

    /**
     * Buffer that stores the response content
     */
    private final StringBuilder respBuf = new StringBuilder(128);

    HttpChannelInboundHandler(Function<ParsedHttpRequest, String> requestHandler) {
        this.requestHandler = requestHandler;
        parsedReq = new ParsedHttpRequest();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // request part
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

            parsedReq.setMethod(request.method().name());
            parsedReq.setUri(request.uri());

            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    parsedReq.addHeader(h.getKey(), h.getValue());
                }
            }

            appendDecoderResult(respBuf, request);
        }

        // content part
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                parsedReq.appendBody(content.toString(CharsetUtil.UTF_8));
                appendDecoderResult(respBuf, httpContent);
            }

            if (msg instanceof LastHttpContent) {
                HttpResponseStatus respStatus = respBuf.length() > 0 ? BAD_REQUEST : OK;

                if (respStatus.equals(OK)) {
                    try {
                        String responseBody = requestHandler.apply(parsedReq);
                        respBuf.append(responseBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                        respBuf.append(e.getMessage());
                        respStatus = BAD_REQUEST;
                    }
                }

                if (!writeResponse(respStatus, ctx)) {
                    // If keep-alive is off, close the connection once the content is fully written
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                            .addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.decoderResult();

        if (result.isSuccess()) {
            return;
        }

        // decoding error
        buf.append("DECODING FAILED: ");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    private boolean writeResponse(HttpResponseStatus respStatus, ChannelHandlerContext ctx) {
        // decide whether to close the connection or not
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // build the response object
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, respStatus,
                Unpooled.copiedBuffer(respBuf.toString(), CharsetUtil.UTF_8)
        );

        // reset buffer
        respBuf.setLength(0);
        parsedReq = new ParsedHttpRequest();

        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                respStatus.equals(OK) ? "application/json; charset=UTF-8" : "text/plain; charset=UTF-8"
        );

        if (keepAlive) {
            // add 'Content-Length' header only for a keep-alive connection
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the response
        ctx.write(response);

        return keepAlive;
    }
}