package com.revolut.dto;

import java.math.BigDecimal;


public class TransferDto
{
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;

    public String getAccountFrom()
    {
        return accountFrom;
    }

    public void setAccountFrom(final String accountFrom)
    {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo()
    {
        return accountTo;
    }

    public void setAccountTo(final String accountTo)
    {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(final BigDecimal amount)
    {
        this.amount = amount;
    }
}
