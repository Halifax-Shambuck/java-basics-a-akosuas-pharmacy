package com.akosua.pharmacy.model;
// A transaction is an immutable historical fact recorded after a successful
// dispense or restock. Using a record enforces that it cannot change after
// creation and gives readable toString output automatically.
// prescriber holds whatever the caller supplied at the call site.
// Only rules that require a prescriber verify it; OTC rules ignore it entirely.

public record Transaction(
        int id,
        String customerName,
        String medicationName,
        int quantity,
        Money total,
        TransactionType type,
        String prescriber
) {
    // Enforces that ids are positive, names are present, quantity is positive,
    // and total is not negative. Total can be zero because restocks carry no
    // monetary value.
    public Transaction {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        if (customerName == null || customerName.isBlank())
            throw new IllegalArgumentException("customer name cannot be blank");
        if (medicationName == null || medicationName.isBlank())
            throw new IllegalArgumentException("medicine name cannot be blank");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (total == null) throw new IllegalArgumentException("total cannot be null");
    }
}

