package org.example.domain;

import lombok.Builder;
import org.example.domain.enumeration.AccountStatus;

import java.math.BigDecimal;

public record AccountState(AccountStatus status, int remainingSuspensions, BigDecimal balance) {

    @Builder
    public AccountState {
    }

    public boolean isClosed() {
        return AccountStatus.CLOSED.equals(status);
    }

    public boolean isSuspended() {
        return AccountStatus.SUSPENDED.equals(status);
    }

    public boolean hasReachedSuspensionCount() {
        return remainingSuspensions < 0;
    }

    public AccountState unsuspend() {
        return new AccountState(AccountStatus.BILLABLE, remainingSuspensions, balance);
    }

    public AccountState suspend() {
        return new AccountState(AccountStatus.SUSPENDED, remainingSuspensions - 1, balance);
    }

    public AccountState close() {
        return new AccountState(AccountStatus.CLOSED, remainingSuspensions, balance);
    }

    public AccountState addToBalance(BigDecimal amount) {
        return new AccountState(status, remainingSuspensions, balance.add(amount));
    }
}
