package com.revolut.dao;

import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.revolut.entity.AccountEntity;


public class PaymentDao
{
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Optional<AccountEntity> findAccountById(final String id)
    {
        final Session session = sessionFactory.openSession();
        final AccountEntity accountEntity = session.get(AccountEntity.class, id);
        session.close();
        return Optional.ofNullable(accountEntity);
    }

    public void saveAccounts(final AccountEntity... accounts)
    {
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (AccountEntity account : accounts)
        {
            session.saveOrUpdate(account);
        }
        session.getTransaction().commit();
        session.close();
    }

}
