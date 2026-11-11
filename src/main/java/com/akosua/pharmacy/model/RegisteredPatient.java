package com.akosua.pharmacy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Registered patients have a phone number on file and receive a 5% discount.
// Dispense history is tracked here because the business only needs it for
// registered patients, not walk-in customers.
public class RegisteredPatient implements Customer {
    private static final double REGISTERED_DISCOUNT = 0.95;

    private final String name;
    private final String phone;
    private final List<String> history = new ArrayList<>();

    public RegisteredPatient(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patient name cannot be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        this.name = name;
        this.phone = phone;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double applyDiscount(double subtotal) {
        // Multiply the subtotal by 0.95 directly. Storing 0.05 and computing
        // 1 - 0.05 can differ in the last bit due to floating-point precision,
        // which would break the golden output test.
        return subtotal * REGISTERED_DISCOUNT;
    }

    @Override
    public void recordDispense(String medicineName, int quantity) {
        history.add("Got " + quantity + " x " + medicineName);
    }

    // Returns a read-only view of the history list. The view reflects later
    // additions but throws UnsupportedOperationException if the caller tries
    // to modify it directly.
    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public String getPhone() {
        return phone;
    }
}
