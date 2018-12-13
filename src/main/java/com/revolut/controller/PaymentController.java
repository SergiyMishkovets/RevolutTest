package com.revolut.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.dto.AccountDto;
import com.revolut.dto.ErrorDto;
import com.revolut.dto.TransferDto;
import com.revolut.entity.AccountEntity;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.InsufficientFundsException;
import com.revolut.exception.MismatchedCurrencyException;
import com.revolut.service.PaymentService;

import io.javalin.Context;
import io.javalin.ExceptionHandler;
import io.javalin.Handler;


public class PaymentController
{
    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private static final PaymentService paymentService = new PaymentService();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Handler makeTransfer = ctx -> {
        TransferDto transferDto = ctx.bodyAsClass(TransferDto.class);
        paymentService.makeTransfer(transferDto.getAccountFrom(), transferDto.getAccountTo(), transferDto.getAmount());
    };

    public static Handler getAccountDetails = ctx -> {
        AccountEntity accountEntity = paymentService.getAccountById(ctx.pathParam("id"));

        AccountDto accountDto = new AccountDto();
        accountDto.setId(accountEntity.getId());
        accountDto.setAmount(accountEntity.getAmount().doubleValue());
        accountDto.setCurrency(accountEntity.getCurrency().toString());

        ctx.result(objectMapper.writeValueAsString(accountDto));
        ctx.status(200);
    };

    public static ExceptionHandler<AccountNotFoundException> handleAccountNotFoundException = (e, ctx) -> {
        LOG.error("There was an error: {}", e.getMessage(), e);

        final ErrorDto errorDto = new ErrorDto();
        errorDto.setCode("err-01");
        errorDto.setMessage(e.getMessage());

        setUpErrorResponse(ctx, errorDto, 404);
    };

    public static ExceptionHandler<InsufficientFundsException> handleInsufficientFundsException = (e, ctx) -> {
        LOG.error("There was an error: {}", e.getMessage(), e);

        final ErrorDto errorDto = new ErrorDto();
        errorDto.setCode("err-02");
        errorDto.setMessage(e.getMessage());
        setUpErrorResponse(ctx, errorDto, 500);
    };

    public static ExceptionHandler<MismatchedCurrencyException> handleMismatchedCurrencyException = (e, ctx) -> {
        LOG.error("There was an error: {}", e.getMessage(), e);

        final ErrorDto errorDto = new ErrorDto();
        errorDto.setCode("err-03");
        errorDto.setMessage(e.getMessage());

        setUpErrorResponse(ctx, errorDto, 500);
    };

    public static ExceptionHandler<Exception> handleException = (e, ctx) -> {
        LOG.error("There was an error: {}", e.getMessage(), e);

        final ErrorDto errorDto = new ErrorDto();
        errorDto.setCode("err-00");
        errorDto.setMessage(e.getMessage());

        setUpErrorResponse(ctx, errorDto, 500);
    };

    private static void setUpErrorResponse(final Context ctx, final ErrorDto errorDto, final int status)
    {
        try
        {
            ctx.status(status);
            ctx.result(objectMapper.writeValueAsString(errorDto));
        }
        catch (JsonProcessingException e)
        {
            LOG.error("There was an error during converting response error into json");
        }
    }
}
