package org.example.domain.command.internal;

import java.math.BigDecimal;

public record UpdateBalanceInternalCommand(BigDecimal amount) implements InternalAccountCommand {
}
