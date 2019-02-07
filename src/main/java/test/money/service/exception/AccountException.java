package test.money.service.exception;

public class AccountException extends RuntimeException {
    private final Long accountNumber;

    public AccountException(String message, Long accountNumber) {
        super(message);
        this.accountNumber = accountNumber;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }
}
