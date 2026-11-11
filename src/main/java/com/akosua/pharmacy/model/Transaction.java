package com.akosua.pharmacy.model;

public record Transaction(
        int id,
        String customerName,
        String medName,
        int qty,
        double total,
        TransactionType type,
        // null for OTC dispenses and restocks; only set when a prescription was verified
        String prescriber
) {
    public Transaction {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        if (customerName == null || customerName.isBlank())
            throw new IllegalArgumentException("customer name cannot be blank");
        if (medName == null || medName.isBlank())
            throw new IllegalArgumentException("medicine name cannot be blank");
        if (qty <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (total < 0) throw new IllegalArgumentException("total cannot be negative");
    }
}

