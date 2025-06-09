package org.example.domain;

import lombok.Builder;
import org.example.domain.command.UpdateBalanceCommand;
import org.example.domain.command.internal.EvaluateAccountStatusInternalCommand;
import org.example.domain.command.internal.InternalAccountCommand;
import org.example.domain.command.internal.UpdateBalanceInternalCommand;
import org.example.domain.enumeration.AccountStatus;
import org.example.domain.event.AccountClosedEvent;
import org.example.domain.event.AccountEvent;
import org.example.domain.event.AccountSuspendedEvent;
import org.example.domain.event.AccountUnsuspendedEvent;
import org.example.domain.event.BalanceUpdatedEvent;
import org.example.domain.exception.AccountClosedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        updateBalanceCommand.getInternalCommands().forEach(internalCommand -> {
            var events = decide(this.state, internalCommand);
            this.state = evolveAll(state, events);
        });
    }

    private List<AccountEvent> decide(AccountState state, InternalAccountCommand internalAccountCommand) {
        return switch (internalAccountCommand) {
            case EvaluateAccountStatusInternalCommand ignored -> getEvaluateStatusEvents(state);
            case UpdateBalanceInternalCommand updateBalanceInternalCommand -> List.of(new BalanceUpdatedEvent(updateBalanceInternalCommand.amount()));
        };
    }

    private ArrayList<AccountEvent> getEvaluateStatusEvents(AccountState state) {
        var events = new ArrayList<AccountEvent>();
        var mustBeSuspended = balanceIsLowerEqualsThan(SUSPENSION_THRESHOLD) && !state.isSuspended();
        if (mustBeSuspended) {
            events.add(new AccountSuspendedEvent());
        }
        if ((mustBeSuspended && state.hasReachedSuspensionCount()) || balanceIsLowerEqualsThan(CLOSING_THRESHOLD)) {
            events.add(new AccountClosedEvent());
        }
        if (balanceIsGreaterThan(SUSPENSION_THRESHOLD) && state.isSuspended()) {
            events.add(new AccountUnsuspendedEvent());
        }
        return events;
    }

    private AccountState evolveAll(AccountState initialState, List<AccountEvent> events) {
        return events.stream().reduce(initialState, this::evolve, (state1, state2) -> state2);
    }

    private AccountState evolve(AccountState initialState, AccountEvent event) {
        return switch (event) {
            case AccountSuspendedEvent ignored -> initialState.suspend();
            case AccountClosedEvent ignored -> initialState.close();
            case AccountUnsuspendedEvent ignored -> initialState.unsuspend();
            case BalanceUpdatedEvent balanceUpdatedEvent -> initialState.addToBalance(balanceUpdatedEvent.amount());

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
