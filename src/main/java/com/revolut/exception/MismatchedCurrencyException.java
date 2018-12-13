package com.revolut.exception;

public class MismatchedCurrencyException extends RuntimeException
{
    public MismatchedCurrencyException(final String message)
    {
        super(message);
    }
}

