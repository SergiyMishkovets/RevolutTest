package com.revolut.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revolut.dao.PaymentDao;
import com.revolut.entity.AccountEntity;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.InsufficientFundsException;
import com.revolut.exception.MismatchedCurrencyException;

import static java.lang.String.format;


public class PaymentService
{
    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private PaymentDao paymentDao = new PaymentDao();

    public void makeTransfer(final String accountFrom, final String accountTo, final BigDecimal amount)
    {
        AccountEntity accountEntityFrom = getAccountById(accountFrom);
        AccountEntity accountEntityTo = getAccountById(accountTo);

        String firstLock;
        String secondLock;
        if (accountFrom.compareTo(accountTo) < 0)
        {
            firstLock = accountFrom.intern();
            secondLock = accountTo.intern();
        }
        else
        {
            firstLock = accountTo.intern();
            secondLock = accountFrom.intern();
        }

        synchronized (firstLock)
        {
            synchronized (secondLock)
            {
                if (amount.compareTo(BigDecimal.ZERO) < 0)
                {
                    throw new IllegalArgumentException("Amount can't be negative");
                }
                else if (accountEntityFrom.getAmount().compareTo(amount) < 0)
                {
                    throw new InsufficientFundsException(format("Insufficient funds on account %s", accountFrom), accountFrom);
                }
                else if (accountEntityFrom.getCurrency() != accountEntityTo.getCurrency())
                {
                    throw new MismatchedCurrencyException("Mismatched  currencies");
                }
                else if (accountFrom.equals(accountTo))
                {
                    LOG.warn("Source and destination accounts are the same one. Skipping");
                }
                else
                {
                    accountEntityFrom.setAmount(accountEntityFrom.getAmount().subtract(amount));
                    accountEntityTo.setAmount(accountEntityTo.getAmount().add(amount));
                    paymentDao.saveAccounts(accountEntityFrom, accountEntityTo);
                }
            }
        }

    }

    public AccountEntity getAccountById(final String id)
    {
        Optional<AccountEntity> accountEntity = paymentDao.findAccountById(id);
        if (accountEntity.isPresent())
        {
            return accountEntity.get();
        }
        else
        {
            throw new AccountNotFoundException(format("Account %s not found", id), id);
        }

    }

    public void setPaymentDao(final PaymentDao paymentDao)
    {
        this.paymentDao = paymentDao;
    }
}
