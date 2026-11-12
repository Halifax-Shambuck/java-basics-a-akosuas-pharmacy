package com.akosua.pharmacy.rules;

public class ControlledRule implements DispensingRule {
    // delegates to PrescriptionRule so the prescriber check lives in one place.
    // Tightening that check automatically applies here too.

    private final PrescriptionRule delegate = new PrescriptionRule();

    @Override
    public String label() {
        return "CONTROLLED";
    }

    @Override
    public void validate(DispenseRequest request) {
        delegate.validate(request);
    }

    @Override
    public String confirmationSuffix(DispenseRequest request) {
        return delegate.confirmationSuffix(request);
    }

}
