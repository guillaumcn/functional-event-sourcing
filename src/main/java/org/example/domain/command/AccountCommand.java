package org.example.domain.command;

public sealed interface AccountCommand permits UpdateBalanceCommand {}
