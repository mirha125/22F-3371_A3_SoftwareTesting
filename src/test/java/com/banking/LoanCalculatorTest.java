package com.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DisplayName("LoanCalculator Tests")
@TestMethodOrder(OrderAnnotation.class)
class LoanCalculatorTest {

    private LoanCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LoanCalculator();
    }

    // ─── Positive Tests ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("should_CalculateCorrectEMI_when_StandardLoanParameters")
    void should_CalculateCorrectEMI_when_StandardLoanParameters() {
        // Arrange
        double principal = 500_000.0;
        double annualRate = 12.0;  // 12%
        int tenureMonths = 60;     // 5 years

        // Act
        double emi = calculator.calculateEMI(principal, annualRate, tenureMonths);

        // Assert — standard formula result: ~11,122.22
        assertEquals(11_122.22, emi, 1.0);
    }

    @Test
    @Order(2)
    @DisplayName("should_CalculateZeroInterest_when_RateIsZero")
    void should_CalculateZeroInterest_when_RateIsZero() {
        // Arrange
        double principal = 120_000.0;
        double annualRate = 0.0;
        int tenureMonths = 12;

        // Act
        double emi = calculator.calculateEMI(principal, annualRate, tenureMonths);

        // Assert
        assertEquals(10_000.0, emi, 0.01);
    }

    @Test
    @Order(3)
    @DisplayName("should_ReturnTrue_when_ApplicantMeetsEligibilityCriteria")
    void should_ReturnTrue_when_ApplicantMeetsEligibilityCriteria() {
        // Arrange: income 1.2M/yr, 500K loan, 0 existing EMIs, 12% rate, 5 years
        boolean eligible = calculator.isEligible(1_200_000, 500_000, 0, 12.0, 60);

        // Assert
        assertTrue(eligible);
    }

    @Test
    @Order(4)
    @DisplayName("should_CalculatePositiveTotalInterest_when_ValidLoanWithRate")
    void should_CalculatePositiveTotalInterest_when_ValidLoanWithRate() {
        // Arrange
        double principal = 300_000.0;
        double annualRate = 10.0;
        int tenureMonths = 36;

        // Act
        double interest = calculator.calculateTotalInterest(principal, annualRate, tenureMonths);

        // Assert
        assertTrue(interest > 0, "Total interest should be positive for non-zero rate.");
    }

    @Test
    @Order(5)
    @DisplayName("should_ReturnMaxEligibleLoan_when_IncomeAndRateProvided")
    void should_ReturnMaxEligibleLoan_when_IncomeAndRateProvided() {
        // Arrange
        double annualIncome = 1_200_000.0;

        // Act
        double maxLoan = calculator.getMaxEligibleLoan(annualIncome, 0, 12.0, 60);

        // Assert
        assertTrue(maxLoan > 0, "Max eligible loan should be positive.");
    }

    // ─── Negative Tests ───────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("should_ThrowException_when_PrincipalIsZero")
    void should_ThrowException_when_PrincipalIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEMI(0, 10.0, 12));
    }

    @Test
    @Order(7)
    @DisplayName("should_ThrowException_when_AnnualRateIsNegative")
    void should_ThrowException_when_AnnualRateIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEMI(100_000, -5.0, 12));
    }

    @Test
    @Order(8)
    @DisplayName("should_ReturnFalse_when_IncomeBelowMinimumThreshold")
    void should_ReturnFalse_when_IncomeBelowMinimumThreshold() {
        // Arrange: income 100K (below 300K minimum)
        boolean eligible = calculator.isEligible(100_000, 50_000, 0, 10.0, 12);

        // Assert
        assertFalse(eligible);
    }

    // ─── Boundary Tests ───────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("should_ThrowException_when_TenureExceedsMaximumLimit")
    void should_ThrowException_when_TenureExceedsMaximumLimit() {
        // Arrange: 361 months > 360 max
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEMI(500_000, 10.0, 361));
    }

    @Test
    @Order(10)
    @DisplayName("should_ThrowException_when_TenureIsZero")
    void should_ThrowException_when_TenureIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEMI(100_000, 10.0, 0));
    }

    @Test
    @Order(11)
    @DisplayName("should_ReturnZeroMaxLoan_when_IncomeIsBelowMinimum")
    void should_ReturnZeroMaxLoan_when_IncomeIsBelowMinimum() {
        double maxLoan = calculator.getMaxEligibleLoan(100_000, 0, 10.0, 12);
        assertEquals(0.0, maxLoan, 0.001);
    }

    @Test
    @Order(12)
    @DisplayName("should_ReturnFalse_when_ExistingEMIsExceedDebtToIncomeRatio")
    void should_ReturnFalse_when_ExistingEMIsExceedDebtToIncomeRatio() {
        // Arrange: high existing EMIs that leave no room for new loan
        boolean eligible = calculator.isEligible(600_000, 200_000, 50_000, 12.0, 60);

        // Assert
        assertFalse(eligible);
    }
}
