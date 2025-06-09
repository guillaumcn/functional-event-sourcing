package org.example.domain.event;

public sealed interface AccountEvent permits AccountClosedEvent, AccountSuspendedEvent, AccountUnsuspendedEvent, NoEvent {}
