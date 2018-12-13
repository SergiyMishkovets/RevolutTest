package com.revolut.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ACCOUNT")
public class AccountEntity
{
    @Id
    private String id;
    @Column
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;


    public AccountEntity()
    {
    }

    public AccountEntity(final String id, final BigDecimal amount, final Currency currency)
    {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(final BigDecimal amount)
    {
        this.amount = amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }
    
}
