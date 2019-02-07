package test.money.service.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private final Long number;
    private final BigDecimal balance;

    public Account(Long number, BigDecimal balance) {
        this.number = number;
        this.balance = balance;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public Long getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(getNumber(), account.getNumber()) &&
                getBalance() != null && getBalance().compareTo(account.getBalance()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getBalance());
    }

    @Override
    public String toString() {
        return "Account{" +
                "number=" + number +
                ", balance=" + balance +
                '}';
    }
}
