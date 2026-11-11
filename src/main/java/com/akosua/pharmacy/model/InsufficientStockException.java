package com.akosua.pharmacy.model;

import com.akosua.pharmacy.rules.DispenseRejectedException;

public class InsufficientStockException extends DispenseRejectedException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
