package com.akosua.pharmacy.model;

public class Medication {
    public String name;
    public double price;
    public int stock;
    public String category; // "OTC" or "PRESCRIPTION"
    public int id;
    static int counter = 1;

    public Medication(String name, double price, int stock, String category) {
        this.id = counter;
        counter++;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}
