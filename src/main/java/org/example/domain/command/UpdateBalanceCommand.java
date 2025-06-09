package org.example.domain.command;

import java.math.BigDecimal;

public record UpdateBalanceCommand(BigDecimal amount) implements AccountCommand {

}
