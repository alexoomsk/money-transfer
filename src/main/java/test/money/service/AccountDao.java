package test.money.service;

import test.money.service.exception.DaoException;
import test.money.service.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDao {

    Optional<Account> getAccountByNumber(long number) throws DaoException;

    void createTransaction(long fromNumber, long toNumber, BigDecimal amount) throws DaoException;
}
