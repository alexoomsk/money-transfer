package test.money.service;

import test.money.service.exception.AccountException;
import test.money.service.exception.DaoException;
import test.money.service.model.Account;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @Test
    void transferMoneyConcurrency() throws ExecutionException, InterruptedException {
        CountDownLatch startSecondThreadBlocker = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        accountService = new AccountServiceImpl(new TestDao(startSecondThreadBlocker));

        Future<Boolean> successTransfer = executor.submit(newTransferTask());
        startSecondThreadBlocker.await();
        Future<Boolean> failedTransfer = executor.submit(newTransferTask());

        if(!successTransfer.get()){
            fail("First thread should not be thrown");
        }
        if(failedTransfer.get()){
            fail("Second thread should throw exception");
        }
        executor.shutdown();
    }

    private Callable<Boolean> newTransferTask(){
        return () -> {
            try {
                accountService.transferMoney(1, 2, BigDecimal.valueOf(60));
            } catch (AccountException e) {
                assertThat(e.getMessage(), is("Source account has not enough money"));
                return false;
            }
            return true;
        };
    }

    static final class TestDao implements AccountDao {
        private final Map<Long, BigDecimal> balances = new ConcurrentHashMap<>();

        private final CountDownLatch latch;

        private TestDao(CountDownLatch latch) {
            this.latch = latch;
            balances.put(1L, BigDecimal.valueOf(100d));
            balances.put(2L, BigDecimal.valueOf(100d));
        }

        @Override
        public Optional<Account> getAccountByNumber(long number) throws DaoException {
            latch.countDown();
            return Optional.of(new Account(number, balances.computeIfAbsent(number, n -> BigDecimal.valueOf(100))));
        }

        @Override
        public void createTransaction(long fromNumber, long toNumber, BigDecimal amount) throws DaoException {
            balances.compute(fromNumber, (from, balance) -> balance.subtract(amount));
            balances.compute(toNumber, (to, balance) -> balance.add(amount));
        }
    }
}
