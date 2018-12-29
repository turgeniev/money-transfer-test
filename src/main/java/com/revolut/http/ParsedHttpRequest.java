package com.revolut.http;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ParsedHttpRequest {
    private String method = "";
    private String uri = "";
    private Map<String, String> headers = new HashMap<>();
    private String body = "";

    ParsedHttpRequest() {
    }

    public ParsedHttpRequest(String method, String uri, Map<String, String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    void appendBody(String body) {
        this.body += body;
    }

    void addHeader(CharSequence key, CharSequence value) {
        headers.put(key.toString(), value.toString());
    }

    @Override
    public String toString() {
        return "ParsedHttpRequest{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
