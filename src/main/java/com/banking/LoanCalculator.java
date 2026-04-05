package com.banking;

/**
 * Calculates loan EMI, total interest, and eligibility checks.
 */
public class LoanCalculator {

    private static final double MIN_ANNUAL_INCOME = 300_000.0;
    private static final double MAX_DEBT_TO_INCOME_RATIO = 0.40;
    private static final int MAX_LOAN_TENURE_MONTHS = 360; // 30 years

    /**
     * Calculates Equated Monthly Installment (EMI).
     *
     * @param principal  Loan principal amount
     * @param annualRate Annual interest rate in percent (e.g., 12 for 12%)
     * @param tenureMonths Loan duration in months
     * @return Monthly EMI amount
     */
    public double calculateEMI(double principal, double annualRate, int tenureMonths) {
        if (principal <= 0) throw new IllegalArgumentException("Principal must be positive.");
        if (annualRate < 0) throw new IllegalArgumentException("Annual rate cannot be negative.");
        if (tenureMonths <= 0) throw new IllegalArgumentException("Tenure must be at least 1 month.");
        if (tenureMonths > MAX_LOAN_TENURE_MONTHS) {
            throw new IllegalArgumentException("Tenure cannot exceed " + MAX_LOAN_TENURE_MONTHS + " months.");
        }

        if (annualRate == 0) {
            return principal / tenureMonths;
        }

        double monthlyRate = annualRate / (12 * 100);
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths))
                / (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return Math.round(emi * 100.0) / 100.0;
    }

    /**
     * Calculates total interest payable over the loan tenure.
     */
    public double calculateTotalInterest(double principal, double annualRate, int tenureMonths) {
        double emi = calculateEMI(principal, annualRate, tenureMonths);
        double totalPayment = emi * tenureMonths;
        return Math.round((totalPayment - principal) * 100.0) / 100.0;
    }

    /**
     * Determines if an applicant is eligible for a loan.
     *
     * @param annualIncome   Applicant's annual income
     * @param requestedLoan  Requested loan amount
     * @param existingEMIs   Total of existing monthly EMI obligations
     * @param annualRate     Annual interest rate in percent
     * @param tenureMonths   Loan duration in months
     * @return true if eligible
     */
    public boolean isEligible(double annualIncome, double requestedLoan,
                               double existingEMIs, double annualRate, int tenureMonths) {
        if (annualIncome < MIN_ANNUAL_INCOME) return false;
        if (requestedLoan <= 0) return false;

        double newEMI = calculateEMI(requestedLoan, annualRate, tenureMonths);
        double monthlyIncome = annualIncome / 12;
        double totalEMIs = existingEMIs + newEMI;
        double debtToIncomeRatio = totalEMIs / monthlyIncome;

        return debtToIncomeRatio <= MAX_DEBT_TO_INCOME_RATIO;
    }

    /**
     * Returns maximum loan amount a person can be eligible for.
     */
    public double getMaxEligibleLoan(double annualIncome, double existingEMIs,
                                      double annualRate, int tenureMonths) {
        if (annualIncome < MIN_ANNUAL_INCOME) return 0;
        double monthlyIncome = annualIncome / 12;
        double maxAllowedEMI = (monthlyIncome * MAX_DEBT_TO_INCOME_RATIO) - existingEMIs;
        if (maxAllowedEMI <= 0) return 0;

        if (annualRate == 0) {
            return maxAllowedEMI * tenureMonths;
        }

        double monthlyRate = annualRate / (12 * 100);
        double maxLoan = maxAllowedEMI * (Math.pow(1 + monthlyRate, tenureMonths) - 1)
                / (monthlyRate * Math.pow(1 + monthlyRate, tenureMonths));
        return Math.round(maxLoan * 100.0) / 100.0;
    }
}
