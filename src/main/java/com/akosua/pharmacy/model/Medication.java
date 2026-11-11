package com.akosua.pharmacy.model;

import com.akosua.pharmacy.rules.DispensingRule;
import com.akosua.pharmacy.rules.DispenseRequest;
import java.util.Objects;

public class Medication {
    private final String name;
    private final double price;
    private int stock;
    private final DispensingRule rule;
    private final int id;
    private static int counter = 1;

    public Medication(String name, double price, int stock, DispensingRule rule) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medication name cannot be blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Starting stock cannot be negative");
        }
        // Objects.requireNonNull throws NullPointerException with a clear message
        // if rule is null, closing the hole where a caller passes null and the
        // medication silently has no dispensing behaviour
        this.rule = Objects.requireNonNull(rule, "Dispensing rule cannot be null");
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.id = counter;
        counter++;
    }

    // stock must never go negative; a wrong count in a pharmacy is a dispensing
    // safety risk
    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Dispense quantity must be positive, got: " + quantity);
        }
        if (quantity > stock) {
            throw new InsufficientStockException(
                "Cannot dispense " + quantity + " units of " + name + ": only " + stock + " in stock");
        }
        stock -= quantity;
    }

    // quantity must be positive; restocking with zero or negative makes no sense
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Restock quantity must be positive, got: " + quantity);
        }
        stock = Math.addExact(stock, quantity);
    }

    // Pharmacy calls this instead of reaching through to the rule directly.
    // The rule decides whether the request is acceptable and throws
    // DispenseRejectedException if not.
    public void validate(DispenseRequest request) {
        rule.validate(request);
    }

    // Returns the label the rule supplies, used in the end-of-day report.
    // Pharmacy never needs to know which rule is active to print the right word.
    public String categoryLabel() {
        return rule.label();
    }

    // Returns the confirmation suffix the rule supplies, for example
    // " (prescribed by Dr Smith)" for prescription or "" for OTC.
    // The leading space is part of the suffix so Pharmacy assembles the line
    // without conditionals.
    public String confirmationSuffix(DispenseRequest request) {
        return rule.confirmationSuffix(request);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getId() {
        return id;
    }
}
