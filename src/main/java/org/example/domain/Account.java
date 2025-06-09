package org.example.domain;

import lombok.Builder;
import org.example.domain.enumeration.AccountStatus;

import java.math.BigDecimal;

public class Account {

    private static final int DEFAULT_MAX_SUSPENSION = 3;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;

    private AccountStatus status;
    private int remainingSuspensions;
    private BigDecimal balance;

    public Account() {
        this.status = AccountStatus.BILLABLE;
        this.remainingSuspensions = DEFAULT_MAX_SUSPENSION;
        this.balance = INITIAL_BALANCE;
    }

    @Builder
    public Account(AccountStatus status, Integer remainingSuspensions, BigDecimal balance) {
        this.status = status != null ? status : AccountStatus.BILLABLE;
        this.remainingSuspensions = remainingSuspensions != null ? remainingSuspensions : DEFAULT_MAX_SUSPENSION;
        this.balance = balance != null ? balance : INITIAL_BALANCE;
    }

    public void updateBalance(BigDecimal amount) {
        // TODO
    }

    public AccountStatus getStatus() {
        return status;
    }

    public int getRemainingSuspensions() {
        return remainingSuspensions;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
