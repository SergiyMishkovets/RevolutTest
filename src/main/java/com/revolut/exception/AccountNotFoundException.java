package com.revolut.exception;

public class AccountNotFoundException extends RuntimeException
{
    private String accountId;

    public AccountNotFoundException(final String message, final String accountId)
    {
        super(message);
        this.accountId = accountId;
    }

    public String getAccountId()
    {
        return accountId;
    }
}
