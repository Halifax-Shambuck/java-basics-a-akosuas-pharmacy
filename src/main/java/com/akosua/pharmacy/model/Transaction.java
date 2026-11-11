package com.akosua.pharmacy.model;

public class Transaction {
    public int id;
    public String customerName;
    public String medName;
    public int qty;
    public double total;
    public TransactionType type; // enum ensures only valid transaction types can be recorded
    static int counter = 1;

    public Transaction(String customerName, String medName, int qty, double total, TransactionType type) {
        this.id = counter;
        counter++;
        this.customerName = customerName;
        this.medName = medName;
        this.qty = qty;
        this.total = total;
        this.type = type;
    }
}
