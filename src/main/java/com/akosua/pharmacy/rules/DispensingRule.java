package com.akosua.pharmacy.rules;

public interface DispensingRule {
    // A rule answers three questions about a dispense attempt: what label to print
    // in the report, whether the request is acceptable, and what confirmation text
    // to append after the customer name.
    String label();

    void validate(DispenseRequest request) throws DispenseRejectedException;

    String confirmationSuffix(DispenseRequest request);
}
