package org.example.domain.command.internal;

public sealed interface InternalAccountCommand permits EvaluateAccountStatusInternalCommand, UpdateBalanceInternalCommand {
}
