package test.money.service;

import test.money.service.exception.AccountException;
import test.money.service.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao;
    private final ConcurrentMap<Long, Lock> locks = new ConcurrentHashMap<>();

    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transferMoney(long fromAccountId, long toAccountId, BigDecimal amount) throws AccountException {
        Lock lock = locks.computeIfAbsent(fromAccountId, accountNumber -> new ReentrantLock());
        lock.lock();
        try {
            Account fromAccount = getAccount(fromAccountId);
            Account toAccount = getAccount(toAccountId);
            transferMoney(fromAccount, toAccount, amount);
        } finally {
            lock.unlock();
        }
    }

    private void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount) throws AccountException {
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new AccountException("Source account has not enough money", fromAccount.getNumber());
            }
            accountDao.createTransaction(fromAccount.getNumber(), toAccount.getNumber(), amount);
    }

    @Override
    public Account getAccount(long accountId) throws AccountException {
        return accountDao.getAccountByNumber(accountId).orElseThrow(() -> new AccountException("Account " + accountId + " does not exist", accountId));
    }
}
