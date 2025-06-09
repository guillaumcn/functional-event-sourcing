package org.example.domain.event;

import java.math.BigDecimal;

public record BalanceUpdatedEvent(BigDecimal amount) implements AccountEvent {

}
