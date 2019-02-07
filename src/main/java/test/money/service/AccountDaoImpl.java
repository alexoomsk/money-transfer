package test.money.service;

import test.money.service.exception.DaoException;
import test.money.service.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;

public class AccountDaoImpl implements AccountDao {
    private final DataSource dataSource;


    private static final String CREATE_TRANSACTION_QUERY = "insert into transactions (from_account_number, to_account_number, amount) values (?, ?, ?)";
    private static final String SELECT_ACCOUNT_QUERY = "SELECT account_number, balance FROM account_balance WHERE account_number = ?";

    public AccountDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Account> getAccountByNumber(long number) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ACCOUNT_QUERY)
        ) {
            statement.setLong(1, number);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(rs.getLong("account_number"), rs.getBigDecimal("balance")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void createTransaction(long fromNumber, long toNumber, BigDecimal amount) throws DaoException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TRANSACTION_QUERY)) {
            statement.setLong(1, fromNumber);
            statement.setLong(2, toNumber);
            statement.setBigDecimal(3, amount);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
