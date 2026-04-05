package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("TransactionHistory Tests")
@TestMethodOrder(OrderAnnotation.class)
class TransactionHistoryTest {

    private TransactionHistory history;

    @BeforeEach
    void setUp() {
        history = new TransactionHistory();
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_AddTransaction_when_ValidTransactionProvided")
    void should_AddTransaction_when_ValidTransactionProvided() {
        // Arrange
        TransactionHistory.Transaction tx = new TransactionHistory.Transaction(
                "TX-001", TransactionHistory.TransactionType.DEPOSIT, 500.0, "Salary");

        // Act
        history.addTransaction(tx);

        // Assert
        assertEquals(1, history.getCount());
    }

    @Test
    @Order(2)
    @DisplayName("should_FilterByType_when_MultipleTransactionsExist")
    void should_FilterByType_when_MultipleTransactionsExist() {
        // Arrange
        history.addTransaction(new TransactionHistory.Transaction("T1",
                TransactionHistory.TransactionType.DEPOSIT, 1000.0, "Credit"));
        history.addTransaction(new TransactionHistory.Transaction("T2",
                TransactionHistory.TransactionType.WITHDRAWAL, 200.0, "ATM"));
        history.addTransaction(new TransactionHistory.Transaction("T3",
                TransactionHistory.TransactionType.DEPOSIT, 500.0, "Bonus"));

        // Act
        var deposits = history.filterByType(TransactionHistory.TransactionType.DEPOSIT);

        // Assert
        assertEquals(2, deposits.size());
    }

    @Test
    @Order(3)
    @DisplayName("should_CalculateTotalAmount_when_SameTypeTransactionsPresent")
    void should_CalculateTotalAmount_when_SameTypeTransactionsPresent() {
        // Arrange
        history.addTransaction(new TransactionHistory.Transaction("T1",
                TransactionHistory.TransactionType.DEPOSIT, 1000.0, "A"));
        history.addTransaction(new TransactionHistory.Transaction("T2",
                TransactionHistory.TransactionType.DEPOSIT, 500.0, "B"));

        // Act
        double total = history.getTotalAmount(TransactionHistory.TransactionType.DEPOSIT);

        // Assert
        assertEquals(1500.0, total, 0.001);
    }

    @Test
    @Order(4)
    @DisplayName("should_FindTransactionById_when_IdExists")
    void should_FindTransactionById_when_IdExists() {
        // Arrange
        history.addTransaction(new TransactionHistory.Transaction("FIND-ME",
                TransactionHistory.TransactionType.TRANSFER, 300.0, "External"));

        // Act
        TransactionHistory.Transaction found = history.findById("FIND-ME");

        // Assert
        assertNotNull(found);
        assertEquals(300.0, found.getAmount(), 0.001);
    }

    @Test
    @Order(5)
    @DisplayName("should_CalculateCorrectNetBalance_when_MixedTransactionsExist")
    void should_CalculateCorrectNetBalance_when_MixedTransactionsExist() {
        // Arrange
        history.addTransaction(new TransactionHistory.Transaction("T1",
                TransactionHistory.TransactionType.DEPOSIT, 2000.0, "In"));
        history.addTransaction(new TransactionHistory.Transaction("T2",
                TransactionHistory.TransactionType.WITHDRAWAL, 500.0, "Out"));

        // Act
        double net = history.getNetBalance();

        // Assert
        assertEquals(1500.0, net, 0.001);
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("should_ThrowException_when_AddingNullTransaction")
    void should_ThrowException_when_AddingNullTransaction() {
        assertThrows(IllegalArgumentException.class, () -> history.addTransaction(null));
    }

    @Test
    @Order(7)
    @DisplayName("should_ThrowException_when_TransactionAmountIsNegative")
    void should_ThrowException_when_TransactionAmountIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionHistory.Transaction("T-BAD",
                        TransactionHistory.TransactionType.DEPOSIT, -100.0, "Bad"));
    }

    @Test
    @Order(8)
    @DisplayName("should_ThrowException_when_FilterTypeIsNull")
    void should_ThrowException_when_FilterTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> history.filterByType(null));
    }

    // ─── Boundary Tests ───────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("should_ReturnZero_when_HistoryIsEmpty")
    void should_ReturnZero_when_HistoryIsEmpty() {
        assertEquals(0, history.getCount());
        assertEquals(0.0, history.getNetBalance(), 0.001);
    }

    @Test
    @Order(10)
    @DisplayName("should_ReturnNull_when_FindByIdNotFound")
    void should_ReturnNull_when_FindByIdNotFound() {
        TransactionHistory.Transaction result = history.findById("NONEXISTENT");
        assertNull(result);
    }

    @Test
    @Order(11)
    @DisplayName("should_ReturnEmptyList_when_NoTransactionsOfRequestedType")
    void should_ReturnEmptyList_when_NoTransactionsOfRequestedType() {
        // Arrange — only deposits, no withdrawals
        history.addTransaction(new TransactionHistory.Transaction("T1",
                TransactionHistory.TransactionType.DEPOSIT, 500.0, "D"));

        // Act
        var withdrawals = history.filterByType(TransactionHistory.TransactionType.WITHDRAWAL);

        // Assert
        assertTrue(withdrawals.isEmpty());
    }

    @Test
    @Order(12)
    @DisplayName("should_ResetCount_when_HistoryIsCleared")
    void should_ResetCount_when_HistoryIsCleared() {
        // Arrange
        history.addTransaction(new TransactionHistory.Transaction("T1",
                TransactionHistory.TransactionType.DEPOSIT, 100.0, "A"));

        // Act
        history.clear();

        // Assert
        assertEquals(0, history.getCount());
    }
}
