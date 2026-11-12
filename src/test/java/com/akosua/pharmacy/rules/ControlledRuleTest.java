package com.akosua.pharmacy.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.akosua.pharmacy.model.Medication;
import com.akosua.pharmacy.model.Money;
import com.akosua.pharmacy.model.Customer;
import com.akosua.pharmacy.service.Pharmacy;
import com.akosua.pharmacy.model.WalkInCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;

// This test builds its own small pharmacy deliberately.
// It does not use PharmacyTracker.main, so the golden output test stays untouched.
public class ControlledRuleTest {

    private Pharmacy pharmacy;
    private Medication controlled;
    private Customer customer;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy("Test Pharmacy");
        controlled = new Medication("Red 10mg", Money.of("50.00"), 20, new ControlledRule());
        customer = new WalkInCustomer("Test Customer");
        pharmacy.addMedication(controlled);
        pharmacy.addCustomer(customer);
    }

    @Test
    void validDispenseReducesStock() {
        pharmacy.dispense(customer, controlled, 1, "Dr. Boateng");
        assertEquals(19, controlled.getStock());
    }

    @Test
    void nullPrescriberIsRejected() {
        pharmacy.dispense(customer, controlled, 1, null);
        assertEquals(20, controlled.getStock());
    }

    @Test
    void categoryLabelIsControlled() {
        assertEquals("CONTROLLED", controlled.categoryLabel());
    }
}
