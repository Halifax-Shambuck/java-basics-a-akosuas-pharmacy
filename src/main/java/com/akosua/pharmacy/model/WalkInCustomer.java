package com.akosua.pharmacy.model;

public class WalkInCustomer implements Customer {
    private final String name;

    public WalkInCustomer(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be blank");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Money applyDiscount(Money subtotal) {
        // Walk-in customers have no discount, so the subtotal is returned unchanged.
        return subtotal;
    }

    @Override
    public void recordDispense(String medicineName, int quantity) {
        // Walk-in customers have no history to maintain, so this does nothing.
        // The business only tracks dispense history for registered patients.
    }
}
