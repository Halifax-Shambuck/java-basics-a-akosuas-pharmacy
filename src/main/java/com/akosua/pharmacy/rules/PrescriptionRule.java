package com.akosua.pharmacy.rules;

public class PrescriptionRule implements DispensingRule {

    @Override
    public String label() {
        return "PRESCRIPTION";
    }

    @Override
    public void validate(DispenseRequest request) {
        if (request.prescriber() == null || request.prescriber().isBlank()) {
            throw new DispenseRejectedException(
                "Cannot dispense " + request.medicineName() + " without a prescriber"
            );
        }
    }

    @Override
    public String confirmationSuffix(DispenseRequest request) {
        return " (prescribed by " + request.prescriber() + ")";
    }
}
