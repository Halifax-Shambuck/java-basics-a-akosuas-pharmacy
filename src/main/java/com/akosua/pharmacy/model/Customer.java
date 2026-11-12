package com.akosua.pharmacy.model;

public interface Customer {
    // Returns the customer's name for the confirmation line and transaction record.
    String getName();

    // Returns the amount owed after applying whatever discount this customer type
    // has. Walk-in customers return the subtotal unchanged.
    Money applyDiscount(Money subtotal);

    // Records that this customer received a dispense. Pharmacy passes the raw
    // facts and each implementation decides what to do with them.
    // WalkInCustomer does nothing. RegisteredPatient adds to its history.
    void recordDispense(String medicineName, int quantity);
}
