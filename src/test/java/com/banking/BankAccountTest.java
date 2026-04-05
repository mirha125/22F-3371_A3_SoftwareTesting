package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("BankAccount Tests")
@TestMethodOrder(OrderAnnotation.class)
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount("ACC-001", "Ali Hassan", 1000.0);
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_IncreaseBalance_when_ValidDepositIsMade")
    void should_IncreaseBalance_when_ValidDepositIsMade() {
        // Arrange
        double depositAmount = 500.0;
        double expectedBalance = 1500.0;

        // Act
        account.deposit(depositAmount);

        // Assert
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }

    @Test
    @Order(2)
    @DisplayName("should_DecreaseBalance_when_ValidWithdrawalIsMade")
    void should_DecreaseBalance_when_ValidWithdrawalIsMade() {
        // Arrange
        double withdrawAmount = 300.0;
        double expectedBalance = 700.0;

        // Act
        account.withdraw(withdrawAmount);

        // Assert
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }

    @Test
    @Order(3)
    @DisplayName("should_TransferFundsSuccessfully_when_SufficientBalance")
    void should_TransferFundsSuccessfully_when_SufficientBalance() {
        // Arrange
        BankAccount target = new BankAccount("ACC-002", "Sara Khan", 200.0);
        double transferAmount = 400.0;

        // Act
        account.transfer(target, transferAmount);

        // Assert
        assertEquals(600.0, account.getBalance(), 0.001);
        assertEquals(600.0, target.getBalance(), 0.001);
    }

    @Test
    @Order(4)
    @DisplayName("should_ReturnCorrectOwnerName_when_AccountIsCreated")
    void should_ReturnCorrectOwnerName_when_AccountIsCreated() {
        // Arrange / Act / Assert
        assertEquals("Ali Hassan", account.getOwnerName());
    }

    @Test
    @Order(5)
    @DisplayName("should_LogTransaction_when_DepositIsMade")
    void should_LogTransaction_when_DepositIsMade() {
        // Arrange
        int initialLogSize = account.getTransactionLog().size();

        // Act
        account.deposit(100.0);

        // Assert
        assertEquals(initialLogSize + 1, account.getTransactionLog().size());
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("should_ThrowException_when_DepositAmountIsZero")
    void should_ThrowException_when_DepositAmountIsZero() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
    }

    @Test
    @Order(7)
    @DisplayName("should_ThrowException_when_WithdrawingMoreThanBalance")
    void should_ThrowException_when_WithdrawingMoreThanBalance() {
        // Arrange
        double overdraftAmount = 2000.0;

        // Act / Assert
        assertThrows(IllegalStateException.class, () -> account.withdraw(overdraftAmount));
    }

    @Test
    @Order(8)
    @DisplayName("should_ThrowException_when_OperatingOnClosedAccount")
    void should_ThrowException_when_OperatingOnClosedAccount() {
        // Arrange
        account.close();

        // Act / Assert
        assertThrows(IllegalStateException.class, () -> account.deposit(100.0));
    }

    // ─── Boundary Tests ───────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("should_WithdrawSuccessfully_when_AmountEqualsExactBalance")
    void should_WithdrawSuccessfully_when_AmountEqualsExactBalance() {
        // Arrange
        double fullBalance = account.getBalance();

        // Act
        account.withdraw(fullBalance);

        // Assert
        assertEquals(0.0, account.getBalance(), 0.001);
    }

    @Test
    @Order(10)
    @DisplayName("should_ThrowException_when_WithdrawalExceedsMaxLimit")
    void should_ThrowException_when_WithdrawalExceedsMaxLimit() {
        // Arrange
        BankAccount richAccount = new BankAccount("ACC-003", "Billal", 100_000.0);
        double overLimit = BankAccount.getMaxWithdrawalLimit() + 1;

        // Act / Assert
        assertThrows(IllegalArgumentException.class, () -> richAccount.withdraw(overLimit));
    }

    @Test
    @Order(11)
    @DisplayName("should_ThrowException_when_CreatingAccountWithNullOwner")
    void should_ThrowException_when_CreatingAccountWithNullOwner() {
        assertThrows(IllegalArgumentException.class,
                () -> new BankAccount("ACC-999", null, 0.0));
    }

    @Test
    @Order(12)
    @DisplayName("should_ThrowException_when_TransferTargetIsNull")
    void should_ThrowException_when_TransferTargetIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> account.transfer(null, 100.0));
    }
}
