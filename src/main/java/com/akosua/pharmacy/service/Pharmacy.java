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
    private final List<Medication> medications = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public Pharmacy(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pharmacy name cannot be blank");
        }
        this.name = name;
    }

    public void addMedication(Medication medication) {
        medications.add(medication);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    // Dispense medicine to a customer. The medication's rule decides whether the
    // request is acceptable and what appears in the confirmation line.
    public void dispense(Customer customer, Medication medication, int quantity, String prescriber) {
        Money total = Money.ZERO;
        try {
            DispenseRequest request = new DispenseRequest(medication.getName(), quantity, prescriber);
            medication.validate(request);
            // Discount applies to the subtotal because that is the business rule.
            Money subtotal = medication.getPrice().multiplyBy(quantity);
            total = customer.applyDiscount(subtotal);

            medication.reduceStock(quantity);
            System.out.println("Dispensed " + quantity + " x " + medication.getName() + " to " + customer.getName()
                    + medication.confirmationSuffix(request) + " for GHS " + total);
            customer.recordDispense(medication.getName(), quantity);
            transactions.add(new Transaction(transactionCounter++, customer.getName(), medication.getName(), quantity, total,
                    TransactionType.DISPENSE, request.prescriber()));

        } catch (DispenseRejectedException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    public void restock(Medication medication, int quantity) {
        medication.increaseStock(quantity);

        transactions.add(new Transaction(transactionCounter++, "(supplier)", medication.getName(), quantity, Money.ZERO,
        TransactionType.RESTOCK, null));

        System.out.println("Restocked " + quantity + " x " + medication.getName());
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
        for (Medication medication : medications) {
            System.out.println("  " + medication.getName() + " (" + medication.categoryLabel() + "): " + medication.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}
