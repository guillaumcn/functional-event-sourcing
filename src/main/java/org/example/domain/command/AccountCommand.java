package org.example.domain.command;

import org.example.domain.command.internal.InternalAccountCommand;

import java.util.List;

public sealed interface AccountCommand permits UpdateBalanceCommand {

    List<InternalAccountCommand> getInternalCommands();
}
