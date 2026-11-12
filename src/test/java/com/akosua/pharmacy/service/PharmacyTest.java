package com.akosua.pharmacy.service;

import com.akosua.pharmacy.model.*;
import com.akosua.pharmacy.rules.OtcRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PharmacyTest {

    private Pharmacy pharmacy;
    private Medication otcMed;
    private Customer walkIn;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy("Test Pharmacy");
        otcMed = new Medication("Paracetamol", Money.of("10.00"), 10, new OtcRule());
        walkIn = new WalkInCustomer("Kofi");
        pharmacy.addMedication(otcMed);
        pharmacy.addCustomer(walkIn);
    }

    @Test
    void overStockDispenseLeavesEverythingUnchanged() {
        pharmacy.dispense(walkIn, otcMed, 11, null);
        assertEquals(10, otcMed.getStock());
        assertEquals(0, pharmacy.getTransactions().size());
    }

    @Test
    void validDispenseAddsOneTransaction() {
        pharmacy.dispense(walkIn, otcMed, 2, null);
        assertEquals(1, pharmacy.getTransactions().size());
        assertEquals(8, otcMed.getStock());
    }

    @Test
    void auditTrailIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> pharmacy.getTransactions().clear());
    }

    @Test
    void transactionIdsAreSequential() {
        pharmacy.dispense(walkIn, otcMed, 1, null);
        pharmacy.restock(otcMed, 5);
        assertEquals(1, pharmacy.getTransactions().get(0).id());
        assertEquals(2, pharmacy.getTransactions().get(1).id());
    }
}
