package com.revolut.dto;

public class AccountDto
{
    private String id;
    private Double amount;
    private String currency;

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(final Double amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(final String currency)
    {
        this.currency = currency;
    }
}
