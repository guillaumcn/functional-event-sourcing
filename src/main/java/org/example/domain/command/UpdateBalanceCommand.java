package org.example.domain.command;

import org.example.domain.command.internal.EvaluateAccountStatusInternalCommand;
import org.example.domain.command.internal.InternalAccountCommand;
import org.example.domain.command.internal.UpdateBalanceInternalCommand;

import java.math.BigDecimal;
import java.util.List;

public record UpdateBalanceCommand(BigDecimal amount) implements AccountCommand {

    @Override
    public List<InternalAccountCommand> getInternalCommands() {
        return List.of(
                new UpdateBalanceInternalCommand(amount),
                new EvaluateAccountStatusInternalCommand()
        );
    }
}
