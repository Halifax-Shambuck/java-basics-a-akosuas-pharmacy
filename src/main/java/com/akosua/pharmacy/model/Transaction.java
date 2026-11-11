package com.akosua.pharmacy.model;

// A transaction is an immutable historical fact recorded after a successful
// dispense or restock. Using a record enforces that it cannot change after
// creation and gives readable toString output automatically.
// prescriber is null for OTC dispenses and restocks. It is only set when a
// prescription was verified by the rule before the dispense went ahead.
public record Transaction(
        int id,
        String customerName,
        String medName,
        int qty,
        double total,
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
        if (medName == null || medName.isBlank())
            throw new IllegalArgumentException("medicine name cannot be blank");
        if (qty <= 0) throw new IllegalArgumentException("quantity must be positive");
        if (total < 0) throw new IllegalArgumentException("total cannot be negative");
    }
}

