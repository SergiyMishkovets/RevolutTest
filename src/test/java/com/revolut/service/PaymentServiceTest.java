package com.revolut.service;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.revolut.dao.PaymentDao;
import com.revolut.entity.AccountEntity;
import com.revolut.entity.Currency;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.InsufficientFundsException;
import com.revolut.exception.MismatchedCurrencyException;


@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest
{
    private static final String ID_ACCOUNT_1 = "11";
    private static final String ID_ACCOUNT_2 = "22";
    private static final String ID_ACCOUNT_3 = "33";
    private static final String ID_ACCOUNT_NOT_FOUND = "not-found";

    private static final BigDecimal AMOUNT_ACCOUNT_1 = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT_ACCOUNT_2 = BigDecimal.valueOf(150);
    private static final BigDecimal AMOUNT_ACCOUNT_3 = BigDecimal.valueOf(200);

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal AMOUNT_INSUFFICIENT = BigDecimal.valueOf(1000);

    @InjectMocks
    private PaymentService testInstance = new PaymentService();

    @Mock
    private PaymentDao mockPaymentDao;

    private AccountEntity account1;
    private AccountEntity account2;
    private AccountEntity account3;

    @Before
    public void setUp()
    {
        account1 = givenAccount(ID_ACCOUNT_1, AMOUNT_ACCOUNT_1, Currency.USD);
        account2 = givenAccount(ID_ACCOUNT_2, AMOUNT_ACCOUNT_2, Currency.USD);
        account3 = givenAccount(ID_ACCOUNT_3, AMOUNT_ACCOUNT_3, Currency.EUR);

        when(mockPaymentDao.findAccountById(ID_ACCOUNT_1)).thenReturn(Optional.of(account1));
        when(mockPaymentDao.findAccountById(ID_ACCOUNT_2)).thenReturn(Optional.of(account2));
        when(mockPaymentDao.findAccountById(ID_ACCOUNT_3)).thenReturn(Optional.of(account3));
        when(mockPaymentDao.findAccountById(ID_ACCOUNT_NOT_FOUND)).thenReturn(Optional.empty());
    }

    @Test
    public void shouldMakeTransferFromOneAccountToAnother()
    {
        testInstance.makeTransfer(ID_ACCOUNT_1, ID_ACCOUNT_2, AMOUNT);

        assertEquals(AMOUNT_ACCOUNT_1.subtract(AMOUNT), account1.getAmount());
    }

    @Test(expected = InsufficientFundsException.class)
    public void shouldThrowExceptionIfThereIsInsufficientAmount()
    {
        testInstance.makeTransfer(ID_ACCOUNT_1, ID_ACCOUNT_2, AMOUNT_INSUFFICIENT);
    }

    @Test(expected = MismatchedCurrencyException.class)
    public void shouldThrowExceptionIfThereIsCurrenciesMismatch()
    {
        testInstance.makeTransfer(ID_ACCOUNT_1, ID_ACCOUNT_3, AMOUNT);
    }

    @Test
    public void shouldDoNothingIfFromAndToAccountsAreTheSame()
    {
        testInstance.makeTransfer(ID_ACCOUNT_1, ID_ACCOUNT_1, AMOUNT);

        assertEquals(AMOUNT_ACCOUNT_1, account1.getAmount());
        verify(mockPaymentDao, never()).saveAccounts(any());
    }

    @Test
    public void shouldGetAccountForIdIfExisted()
    {
        AccountEntity accountEntity = testInstance.getAccountById(ID_ACCOUNT_1);

        assertNotNull(accountEntity);
        assertEquals(ID_ACCOUNT_1, accountEntity.getId());
    }

    @Test(expected = AccountNotFoundException.class)
    public void shouldThrowExceptionIfAccountIdNotFound()
    {
        testInstance.getAccountById(ID_ACCOUNT_NOT_FOUND);
    }

    private AccountEntity givenAccount(final String id, final BigDecimal amount, final Currency currency)
    {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setAmount(amount);
        accountEntity.setCurrency(currency);
        return accountEntity;
    }
}
