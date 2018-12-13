package com.revolut;

import java.math.BigDecimal;

import com.revolut.controller.PaymentController;
import com.revolut.dao.PaymentDao;
import com.revolut.entity.AccountEntity;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.InsufficientFundsException;
import com.revolut.exception.MismatchedCurrencyException;

import io.javalin.Javalin;

import static com.revolut.entity.Currency.EUR;
import static com.revolut.entity.Currency.USD;


public class Application
{
    private static final int WEB_PORT = 8080;
    private static final PaymentDao paymentDao = new PaymentDao();


    public static void main(String... args)
    {
        initDb();

        Javalin app = Javalin.create().start(WEB_PORT);

        app.post("/make-transfer", PaymentController.makeTransfer);
        app.get("/account/:id", PaymentController.getAccountDetails);

        app.exception(InsufficientFundsException.class, PaymentController.handleInsufficientFundsException);
        app.exception(AccountNotFoundException.class, PaymentController.handleAccountNotFoundException);
        app.exception(MismatchedCurrencyException.class, PaymentController.handleMismatchedCurrencyException);
        app.exception(Exception.class, PaymentController.handleException);
    }

    private static void initDb()
    {
        paymentDao.saveAccounts(new AccountEntity("11U", BigDecimal.valueOf(100), USD));
        paymentDao.saveAccounts(new AccountEntity("22U", BigDecimal.valueOf(150), USD));
        paymentDao.saveAccounts(new AccountEntity("11E", BigDecimal.valueOf(300), EUR));
        paymentDao.saveAccounts(new AccountEntity("22E", BigDecimal.valueOf(200), EUR));
    }
}
