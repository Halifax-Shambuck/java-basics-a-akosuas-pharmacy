package com.akosua.pharmacy.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    public String name;
    public String kind; // "WALK_IN" or "REGISTERED"
    public String phone;
    public int id;
    public List<String> history = new ArrayList<>();
    static int counter = 1;

    public Customer(String name, String kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }
}
