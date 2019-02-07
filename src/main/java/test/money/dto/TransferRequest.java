package test.money.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private final Long fromAccountNumber;
    private final Long toAccountNumber;
    private final BigDecimal amount;

    public TransferRequest(Long fromAccountNumber, Long toAccountNumber, BigDecimal amount) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public Long getFromAccountNumber() {
        return fromAccountNumber;
    }

    public Long getToAccountNumber() {
        return toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

}
