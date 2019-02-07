package test.money.service;

import test.money.DBHelper;
import test.money.service.model.Account;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDaoImplTest {
    private AccountDaoImpl accountDao;

    @BeforeEach
    void setUp() throws SQLException {
        DBHelper.reInitDB();
        accountDao = new AccountDaoImpl(DBHelper.getDataSource());
        try(Connection connection = DBHelper.getConnection();
            Statement statement = connection.createStatement()) {
            statement.execute("insert into transactions (from_account_number, to_account_number, amount) values (1,2,34.5634);\n" +
                    "insert into transactions (from_account_number, to_account_number, amount) values (3,1,50.7313);\n" +
                    "insert into transactions (from_account_number, to_account_number, amount) values (2,3,130);\n" +
                    "insert into transactions (from_account_number, to_account_number, amount) values (1,2,68);");
        }
    }

    @Test
    void getExistedAccountById() {
        Optional<Account> account = accountDao.getAccountByNumber(1L);
        assertTrue(account.isPresent());
        assertThat(account.get(), is(new Account(1L, BigDecimal.valueOf(48.1679d))));
    }

    @Test
    void getNotExistedAccountById() {
        Optional<Account> account = accountDao.getAccountByNumber(4L);
        assertFalse(account.isPresent());
    }

    @Test
    void createTransaction() {
        accountDao.createTransaction(1L, 2L, BigDecimal.valueOf(0.16));
        Optional<Account> account = accountDao.getAccountByNumber(1L);
        assertTrue(account.isPresent());
        assertThat(account.get(), is(new Account(1L, BigDecimal.valueOf(48.0079d))));
    }


}
