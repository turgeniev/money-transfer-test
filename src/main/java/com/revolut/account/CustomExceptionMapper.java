package com.revolut.account;

import com.revolut.account.service.AccountNotFoundException;
import com.revolut.account.service.ClientException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
class CustomExceptionMapper implements ExceptionMapper<Exception> {

    public Response toResponse(Exception ex) {
        int statusCode = 500;

        if (ex instanceof AccountNotFoundException) {
            statusCode = 404;
        } else if (
                ex instanceof ClientException ||
                ex instanceof IllegalArgumentException ||
                ex instanceof IllegalStateException
        ) {
            statusCode = 400;
        }

        return Response.status(statusCode)
                .entity(ex.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}