package org.example.domain;

import lombok.Builder;
import org.example.domain.command.AccountCommand;
import org.example.domain.command.UpdateBalanceCommand;
import org.example.domain.enumeration.AccountStatus;
import org.example.domain.event.AccountClosedEvent;
import org.example.domain.event.AccountEvent;
import org.example.domain.event.AccountSuspendedEvent;
import org.example.domain.event.AccountUnsuspendedEvent;
import org.example.domain.event.NoEvent;
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

    public void handleCommand(UpdateBalanceCommand updateBalanceCommand) {
        assertIsNotClosed();
        this.state = state.addToBalance(updateBalanceCommand.amount());

        AccountEvent event = decide(state, updateBalanceCommand);
        this.state = evolve(state, event);
    }

    private AccountEvent decide(AccountState state, AccountCommand accountCommand) {
        // PB1 : SUSPENDED event, can be overridden by CLOSED event, maybe I need to have the SUSPENDED information too
        AccountEvent event = new NoEvent();

        var mustBeSuspended = balanceIsLowerEqualsThan(SUSPENSION_THRESHOLD) && !state.isSuspended();
        if (mustBeSuspended) {
            event = new AccountSuspendedEvent();
        }
        if ((mustBeSuspended && state.hasReachedSuspensionCount()) || balanceIsLowerEqualsThan(CLOSING_THRESHOLD)) {
            event = new AccountClosedEvent();
        }
        if (balanceIsGreaterThan(SUSPENSION_THRESHOLD) && state.isSuspended()) {
            event = new AccountUnsuspendedEvent();
        }
        return event;
    }

    private AccountState evolve(AccountState initialState, AccountEvent event) {
        return switch (event) {
            case NoEvent ignored -> initialState;
            case AccountSuspendedEvent ignored -> initialState.suspend();
            case AccountClosedEvent ignored -> initialState.close();
            case AccountUnsuspendedEvent ignored -> initialState.unsuspend();
        };
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
