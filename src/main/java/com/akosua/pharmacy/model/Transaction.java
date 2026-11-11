package com.akosua.pharmacy.model;

public class Transaction {
    public int id;
    public String customerName;
    public String medName;
    public int qty;
    public double total;
    public String type; // "DISPENSE" or "RESTOCK"
    static int counter = 1;

    public Transaction(String customerName, String medName, int qty, double total, String type) {
        this.id = counter;
        counter++;
        this.customerName = customerName;
        this.medName = medName;
        this.qty = qty;
        this.total = total;
        this.type = type;
    }
}
