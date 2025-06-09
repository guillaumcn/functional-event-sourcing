package org.example.domain;

import org.example.domain.enumeration.AccountStatus;
import org.example.domain.exception.AccountClosedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @Test
    public void accountCreated_hasInitialState() {
        var account = new Account();
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(3, account.getRemainingSuspensions());
        assertEquals(AccountStatus.BILLABLE, account.getStatus());
    }

    @Nested
    class Invoice {

        @Test
        public void accountWithStatusClosed_throwsClosedException() {
            var account = Account.builder()
                                 .status(AccountStatus.CLOSED)
                                 .build();
            assertThrows(AccountClosedException.class, () -> account.updateBalance(BigDecimal.TEN.negate()));
        }

        @Test
        public void accountWithStatusBillable_amountIsSubtracted() {
            var account = Account.builder()
                                 .balance(BigDecimal.valueOf(20))
                                 .build();
            account.updateBalance(BigDecimal.TEN.negate());
            assertEquals(BigDecimal.TEN, account.getBalance());
        }

        @Test
        public void balanceReachesFirstThresholdWithSuspendedAccount_accountStaysSuspended() {
            var currentSuspensionRemaining = 2;
            var account = Account.builder()
                                 .status(AccountStatus.SUSPENDED)
                                 .balance(BigDecimal.valueOf(-90))
                                 .remainingSuspensions(currentSuspensionRemaining)
                                 .build();
            account.updateBalance(BigDecimal.valueOf(20).negate());
            assertEquals(BigDecimal.valueOf(-110), account.getBalance());
            assertEquals(AccountStatus.SUSPENDED, account.getStatus());
            assertEquals(currentSuspensionRemaining, account.getRemainingSuspensions());
        }

        @Test
        public void balanceReachesFirstThresholdWithNotSuspendedAccount_accountIsSuspended() {
            var currentSuspensionRemaining = 2;
            var account = Account.builder()
                                 .status(AccountStatus.BILLABLE)
                                 .balance(BigDecimal.valueOf(-90))
                                 .remainingSuspensions(currentSuspensionRemaining)
                                 .build();
            account.updateBalance(BigDecimal.valueOf(20).negate());
            assertEquals(BigDecimal.valueOf(-110), account.getBalance());
            assertEquals(AccountStatus.SUSPENDED, account.getStatus());
            assertEquals(1, account.getRemainingSuspensions());
        }

        @Test
        public void balanceReachesSecondThreshold_accountIsClosed() {
            var account = Account.builder()
                                 .status(AccountStatus.SUSPENDED)
                                 .balance(BigDecimal.valueOf(-450))
                                 .build();
            account.updateBalance(BigDecimal.valueOf(60).negate());
            assertEquals(BigDecimal.valueOf(-510), account.getBalance());
            assertEquals(AccountStatus.CLOSED, account.getStatus());
        }

        @Test
        public void balanceReachesFirstThresholdForTheForthTime_accountIsClosed() {
            var currentSuspensionRemaining = 0;
            var account = Account.builder()
                                 .status(AccountStatus.BILLABLE)
                                 .balance(BigDecimal.valueOf(-90))
                                 .remainingSuspensions(currentSuspensionRemaining)
                                 .build();
            account.updateBalance(BigDecimal.valueOf(20).negate());
            assertEquals(BigDecimal.valueOf(-110), account.getBalance());
            assertEquals(AccountStatus.CLOSED, account.getStatus());
        }
    }

    @Nested
    class Pay {

        @Test
        public void accountWithStatusClosed_throwsClosedException() {
            var account = Account.builder()
                                 .status(AccountStatus.CLOSED)
                                 .build();
            assertThrows(AccountClosedException.class, () -> account.updateBalance(BigDecimal.TEN));
        }

        @Test
        public void accountWithStatusBillable_amountIsAdded() {
            var account = Account.builder()
                                 .status(AccountStatus.BILLABLE)
                                 .balance(BigDecimal.valueOf(-20))
                                 .build();
            account.updateBalance(BigDecimal.TEN);
            assertEquals(AccountStatus.BILLABLE, account.getStatus());
            assertEquals(BigDecimal.valueOf(-10), account.getBalance());
        }

        @Test
        public void suspendedAccountReachFirstThreshold_accountIsUnsuspended() {
            var currentSuspensionRemaining = 2;
            var account = Account.builder()
                                 .status(AccountStatus.SUSPENDED)
                                 .remainingSuspensions(currentSuspensionRemaining)
                                 .balance(BigDecimal.valueOf(-110))
                                 .build();
            account.updateBalance(BigDecimal.valueOf(20));
            assertEquals(AccountStatus.BILLABLE, account.getStatus());
            assertEquals(BigDecimal.valueOf(-90), account.getBalance());
            assertEquals(currentSuspensionRemaining, account.getRemainingSuspensions());
        }
    }

}
