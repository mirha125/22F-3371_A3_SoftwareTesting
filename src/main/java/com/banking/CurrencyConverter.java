package com.banking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Converts monetary amounts between supported currencies.
 * All rates are relative to PKR (Pakistani Rupee) as the base currency.
 */
public class CurrencyConverter {

    private final Map<String, Double> ratesFromPKR; // currency -> rate (1 PKR = X currency)

    public CurrencyConverter() {
        ratesFromPKR = new HashMap<>();
        // Default approximate rates (1 PKR = ?)
        ratesFromPKR.put("PKR", 1.0);
        ratesFromPKR.put("USD", 0.0036);
        ratesFromPKR.put("EUR", 0.0033);
        ratesFromPKR.put("GBP", 0.0028);
        ratesFromPKR.put("SAR", 0.0135);
        ratesFromPKR.put("AED", 0.0132);
    }

    /**
     * Converts an amount from one currency to another.
     */
    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
        if (fromCurrency == null || toCurrency == null) {
            throw new IllegalArgumentException("Currency codes cannot be null.");
        }
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();
        if (!ratesFromPKR.containsKey(from)) {
            throw new IllegalArgumentException("Unsupported currency: " + fromCurrency);
        }
        if (!ratesFromPKR.containsKey(to)) {
            throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }

        // Convert to PKR first, then to target currency
        double amountInPKR = amount / ratesFromPKR.get(from);
        double result = amountInPKR * ratesFromPKR.get(to);
        return Math.round(result * 100.0) / 100.0;
    }

    /**
     * Updates or adds an exchange rate.
     *
     * @param currency Currency code (e.g., "USD")
     * @param rateFromPKR How much 1 PKR is worth in that currency
     */
    public void setRate(String currency, double rateFromPKR) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency code cannot be null or blank.");
        }
        if (rateFromPKR <= 0) {
            throw new IllegalArgumentException("Rate must be positive.");
        }
        this.ratesFromPKR.put(currency.toUpperCase(), rateFromPKR);
    }

    public Set<String> getSupportedCurrencies() {
        return Collections.unmodifiableSet(ratesFromPKR.keySet());
    }

    public double getRate(String currency) {
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null.");
        String key = currency.toUpperCase();
        if (!ratesFromPKR.containsKey(key)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        return ratesFromPKR.get(key);
    }
}
