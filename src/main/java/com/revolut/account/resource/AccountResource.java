package com.revolut.account.resource;

import com.revolut.account.model.AccountID;
import com.revolut.account.service.AccountService;
import com.revolut.account.service.AccountServiceConfig;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Singleton
@Path(AccountResource.ACCOUNTS_PATH)
public class AccountResource {

    public static final String ACCOUNTS_PATH = "v1/accounts";

    private AccountService accountService;

    public AccountResource() {
        accountService = AccountServiceConfig.service();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResp getById(@PathParam("id") String accountId) {

        return AccountResp.createFrom(
                accountService.getAccount(new AccountID(accountId))
        );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResp create(CreateAccountReq createRequest) {

        return AccountResp.createFrom(
                accountService.create(createRequest.getCurrencyType())
        );
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResp getById(@PathParam("id") String accountId, PutAmountReq topUpRequest) {

        return AccountResp.createFrom(
                accountService.topUp(new AccountID(accountId), topUpRequest.getAmount())
        );
    }

    @PUT
    @Path("{fromId}/{toId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResp transfer(
            @PathParam("fromId") String fromAccountId,
            @PathParam("toId") String toAccountId,
            PutAmountReq transferRequest
    ) {

        return AccountResp.createFrom(
                accountService.transfer(
                        new AccountID(fromAccountId), new AccountID(toAccountId), transferRequest.getAmount()
                )
        );
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResp close(@PathParam("id") String accountId) {

        return AccountResp.createFrom(
            accountService.close(new AccountID(accountId))
        );
    }

}