package com.banking;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account with basic operations.
 */
public class BankAccount {

    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private boolean active;
    private final List<String> transactionLog;
    private static final double MAX_WITHDRAWAL_LIMIT = 10_000.0;
    private static final double MIN_BALANCE = 0.0;

    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank.");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name cannot be null or blank.");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.active = true;
        this.transactionLog = new ArrayList<>();
        transactionLog.add(String.format("Account opened with balance: %.2f", initialBalance));
    }

    public void deposit(double amount) {
        if (!active) throw new IllegalStateException("Account is not active.");
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
        transactionLog.add(String.format("Deposited: %.2f | Balance: %.2f", amount, balance));
    }

    public void withdraw(double amount) {
        if (!active) throw new IllegalStateException("Account is not active.");
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amount > MAX_WITHDRAWAL_LIMIT) {
            throw new IllegalArgumentException("Amount exceeds single withdrawal limit of " + MAX_WITHDRAWAL_LIMIT);
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds.");
        }
        balance -= amount;
        transactionLog.add(String.format("Withdrew: %.2f | Balance: %.2f", amount, balance));
    }

    public void transfer(BankAccount target, double amount) {
        if (target == null) throw new IllegalArgumentException("Target account cannot be null.");
        this.withdraw(amount);
        target.deposit(amount);
        transactionLog.add(String.format("Transferred: %.2f to account %s", amount, target.getAccountNumber()));
    }

    public void close() {
        this.active = false;
        transactionLog.add("Account closed.");
    }

    public double getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public boolean isActive() { return active; }
    public List<String> getTransactionLog() { return new ArrayList<>(transactionLog); }
    public static double getMaxWithdrawalLimit() { return MAX_WITHDRAWAL_LIMIT; }
    public static double getMinBalance() { return MIN_BALANCE; }
}
