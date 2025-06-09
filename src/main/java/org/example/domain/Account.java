package org.example.domain;

import lombok.Builder;
import org.example.domain.enumeration.AccountStatus;
import org.example.domain.exception.AccountClosedException;

import java.math.BigDecimal;

public class Account {

    private static final BigDecimal CLOSING_THRESHOLD = BigDecimal.valueOf(-500);
    private static final BigDecimal SUSPENSION_THRESHOLD = BigDecimal.valueOf(-100);
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
        if (AccountStatus.CLOSED == status) {
            throw new AccountClosedException();
        }
        this.balance = balance.add(amount);
        if (balanceIsLowerEqualsThan(SUSPENSION_THRESHOLD) && !AccountStatus.SUSPENDED.equals(status)) {
            this.status = AccountStatus.SUSPENDED;
            this.remainingSuspensions--;
        }
        if (remainingSuspensions < 0 || balanceIsLowerEqualsThan(CLOSING_THRESHOLD)) {
            this.status = AccountStatus.CLOSED;
        }
        if (balanceIsGreaterThan(SUSPENSION_THRESHOLD) && AccountStatus.SUSPENDED == status) {
            this.status = AccountStatus.BILLABLE;
        }
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

    private boolean balanceIsLowerEqualsThan(BigDecimal value) {
        return balance.compareTo(value) <= 0;
    }

    private boolean balanceIsGreaterThan(BigDecimal value) {
        return balance.compareTo(value) > 0;
    }

}
