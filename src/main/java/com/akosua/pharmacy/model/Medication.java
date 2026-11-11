package com.akosua.pharmacy.model;

public class Medication {
    private final String name;
    private final double price;
    private int stock;
    private final String category; // "OTC" or "PRESCRIPTION"
    private final int id;
    static int counter = 1;

    public Medication(String name, double price, int stock, String category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Medication name cannot be blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Starting stock cannot be negative");
        }

        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.id = counter;
        counter++;
    }

    // stock must never go negative; a wrong count in a pharmacy is a dispensing
    // safety risk

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Dispense quantity must be positive, got: " + quantity);
        }

        if (quantity > stock) {
            throw new InsufficientStockException(
                    "Cannot dispense " + quantity + " units of " + name + ": only " + stock + " in stock");
        }

        stock -= quantity;
    }

    // quantity must be positive; restocking with zero or negative makes no sense

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive, got: " + quantity);
        }

        stock = Math.addExact(stock, quantity);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }
    public int getId() {
        return id;
    }

}
