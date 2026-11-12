package com.akosua.pharmacy.service;

import com.akosua.pharmacy.model.Medication;
import com.akosua.pharmacy.model.Money;
import com.akosua.pharmacy.model.Customer;
import com.akosua.pharmacy.model.Transaction;
import com.akosua.pharmacy.model.TransactionType;
import com.akosua.pharmacy.rules.DispenseRequest;
import com.akosua.pharmacy.rules.DispenseRejectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pharmacy {
    // Pharmacy owns the transaction id counter so ids are unique across all
    // transactions. No other class can increment it.
    private int transactionCounter = 1;
    private final String name;
    private final List<Medication> meds = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public Pharmacy(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pharmacy name cannot be blank");
        }
        this.name = name;
    }

    public void addMedication(Medication m) {
        meds.add(m);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    // Dispense medicine to a customer. The medication's rule decides whether the
    // request is acceptable and what appears in the confirmation line.
    public void dispense(Customer c, Medication m, int qty, String prescriber) {
        Money total = Money.ZERO;
        try {
            DispenseRequest request = new DispenseRequest(m.getName(), qty, prescriber);
            m.validate(request);
            // Discount applies to the subtotal because that is the business rule.
            Money subtotal = m.getPrice().multiplyBy(qty);
            total = c.applyDiscount(subtotal);

            m.reduceStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.getName()
                    + m.confirmationSuffix(request) + " for GHS " + total);
            c.recordDispense(m.getName(), qty);
            transactions.add(new Transaction(transactionCounter++, c.getName(), m.getName(), qty, total,
                    TransactionType.DISPENSE, request.prescriber()));

        } catch (DispenseRejectedException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public void restock(Medication m, int qty) {
        m.increaseStock(qty);

        transactions.add(new Transaction(transactionCounter++, "(supplier)", m.getName(), qty, Money.ZERO,
        TransactionType.RESTOCK, null));

        System.out.println("Restocked " + qty + " x " + m.getName());
    }

    // Returns an unmodifiable view of the transaction list. The view reflects
    // later additions but throws UnsupportedOperationException if the caller
    // tries to modify it, protecting the audit trail from outside changes.
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.getName() + " (" + m.categoryLabel() + "): " + m.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}
