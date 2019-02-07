package test.money.service;

import test.money.service.exception.AccountException;
import test.money.service.model.Account;

import java.math.BigDecimal;

public interface AccountService {

    void transferMoney(long fromAccountId, long toAccountId, BigDecimal amount) throws AccountException;

    Account getAccount(long accountId) throws AccountException;
}
