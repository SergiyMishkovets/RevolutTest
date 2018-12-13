package com.revolut.exception;

public class InsufficientFundsException extends RuntimeException
{
    private String accountId;

    public InsufficientFundsException(final String message, final String accountId)
    {
        super(message);
        this.accountId = accountId;
    }

    public String getAccountId()
    {
        return accountId;
    }

}
