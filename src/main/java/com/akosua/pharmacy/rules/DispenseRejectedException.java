package com.akosua.pharmacy.rules;

// Base exception for any dispense rejection. Catching this type covers all
// subclasses, so Pharmacy needs only one catch block regardless of why the
// dispense failed.
public class DispenseRejectedException extends RuntimeException {
    public DispenseRejectedException(String message) {
        super(message);
    }
}
