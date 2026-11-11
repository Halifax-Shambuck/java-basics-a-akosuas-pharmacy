package com.akosua.pharmacy.rules;

// Carries the facts a rule needs to validate one dispense attempt.
// Holds the medicine name rather than the Medication object so the rules
// package does not depend on the model package, keeping the dependency
// direction one-way.
public record DispenseRequest(
    String medicineName,
    int quantity,
    String prescriber
) { }
