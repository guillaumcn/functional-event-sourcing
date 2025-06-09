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

    private AccountState state;

    public Account() {
        this.state = new AccountState(AccountStatus.BILLABLE, DEFAULT_MAX_SUSPENSION, INITIAL_BALANCE);
    }

    @Builder
    public Account(AccountStatus status, Integer remainingSuspensions, BigDecimal balance) {
        this.state = new AccountState(
                status != null ? status : AccountStatus.BILLABLE,
                remainingSuspensions != null ? remainingSuspensions : DEFAULT_MAX_SUSPENSION,
                balance != null ? balance : INITIAL_BALANCE
        );
    }

    public void updateBalance(BigDecimal amount) {
        assertIsNotClosed();
        this.state = state.addToBalance(amount);
        if (balanceIsLowerEqualsThan(SUSPENSION_THRESHOLD) && !state.isSuspended()) {
            this.state = state.suspend();
        }
        if (state.hasReachedSuspensionCount() || balanceIsLowerEqualsThan(CLOSING_THRESHOLD)) {
            this.state = state.close();
        }
        if (balanceIsGreaterThan(SUSPENSION_THRESHOLD) && state.isSuspended()) {
            this.state = state.unsuspend();
        }
    }

    public AccountStatus getStatus() {
        return state.status();
    }

    public int getRemainingSuspensions() {
        return state.remainingSuspensions();
    }

    public BigDecimal getBalance() {
        return state.balance();
    }

    private void assertIsNotClosed() {
        if (state.isClosed()) {
            throw new AccountClosedException();
        }
    }

    private boolean balanceIsLowerEqualsThan(BigDecimal value) {
        return state.balance().compareTo(value) <= 0;
    }

    private boolean balanceIsGreaterThan(BigDecimal value) {
        return state.balance().compareTo(value) > 0;
    }

}
