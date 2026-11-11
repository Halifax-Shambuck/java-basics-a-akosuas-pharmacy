package com.akosua.pharmacy.service;

import com.akosua.pharmacy.model.Medication;
import com.akosua.pharmacy.model.Customer;
import com.akosua.pharmacy.model.InsufficientStockException;
import com.akosua.pharmacy.model.Transaction;
import com.akosua.pharmacy.model.TransactionType;
import java.util.ArrayList;
import java.util.List;

public class Pharmacy {
    public String name;
    public List<Medication> meds = new ArrayList<>();
    public List<Customer> customers = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Pharmacy(String name) {
        this.name = name;
    }

    public void addMedication(Medication m) {
        meds.add(m);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    // Dispense medicine to a customer. The rules depend on the kind of medicine.
    public void dispense(Customer c, Medication m, int qty, String prescriber) {
        double total = 0;
        try {
            if (m.getCategory().equals("OTC")) {
                total = m.getPrice() * qty;
                if (c.kind.equals("REGISTERED")) {
                    total = total * 0.95; // registered patients get 5% off
                }
                m.reduceStock(qty);
                System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.name
                        + " for GHS " + total);
            } else if (m.getCategory().equals("PRESCRIPTION")) {
                if (prescriber == null) {
                    System.out.println("Cannot dispense " + m.getName() + " without a prescriber");
                    return;
                }
                total = m.getPrice() * qty;
                if (c.kind.equals("REGISTERED")) {
                    total = total * 0.95; // registered patients get 5% off
                }
                m.reduceStock(qty);
                System.out.println("Dispensed " + qty + " x " + m.getName() + " to " + c.name
                        + " (prescribed by " + prescriber + ") for GHS " + total);
            }
        } catch (InsufficientStockException e) {
            System.out.println(e.getMessage());
            return;
        }
        c.history.add("Got " + qty + " x " + m.getName());
        transactions.add(new Transaction(c.name, m.getName(), qty, total, TransactionType.DISPENSE));
    }

    public void restock(Medication m, int qty) {
        m.increaseStock(qty);

        transactions.add(new Transaction("(supplier)", m.getName(), qty, 0, TransactionType.RESTOCK));
        System.out.println("Restocked " + qty + " x " + m.getName());
    }

    public List<Transaction> getTransactions() {
        return transactions; // hand the list back so callers can read it
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.getName() + " (" + m.getCategory() + "): " + m.getStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}
