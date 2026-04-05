package com.banking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maintains a record of financial transactions.
 */
public class TransactionHistory {

    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER }

    public static class Transaction {
        private final String id;
        private final TransactionType type;
        private final double amount;
        private final LocalDateTime timestamp;
        private final String description;

        public Transaction(String id, TransactionType type, double amount, String description) {
            if (amount <= 0) throw new IllegalArgumentException("Transaction amount must be positive.");
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.timestamp = LocalDateTime.now();
            this.description = description;
        }

        public String getId() { return id; }
        public TransactionType getType() { return type; }
        public double getAmount() { return amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return String.format("[%s] %s: %.2f - %s", timestamp, type, amount, description);
        }
    }

    private final List<Transaction> transactions;

    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null.");
        transactions.add(transaction);
    }

    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Transaction> filterByType(TransactionType type) {
        if (type == null) throw new IllegalArgumentException("Transaction type cannot be null.");
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    public double getTotalAmount(TransactionType type) {
        return filterByType(type).stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getNetBalance() {
        double deposits = getTotalAmount(TransactionType.DEPOSIT);
        double withdrawals = getTotalAmount(TransactionType.WITHDRAWAL);
        double transfers = getTotalAmount(TransactionType.TRANSFER);
        return deposits - withdrawals - transfers;
    }

    public Transaction findById(String id) {
        return transactions.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public int getCount() {
        return transactions.size();
    }

    public void clear() {
        transactions.clear();
    }
}
