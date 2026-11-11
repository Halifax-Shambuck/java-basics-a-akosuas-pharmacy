package com.akosua.pharmacy.rules;

public class OtcRule implements DispensingRule {

    @Override
    public String label() {
        return "OTC";
    }

    @Override
    public void validate(DispenseRequest request) {
        // OTC always passes, nothing to check
    }

    @Override
    public String confirmationSuffix(DispenseRequest request) {
        return "";
    }
}
