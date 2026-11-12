package com.akosua.pharmacy.model;

import com.akosua.pharmacy.rules.OtcRule;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MedicationTest {

    private Medication med() {
        return new Medication("Paracetamol", Money.of("10.00"), 10, new OtcRule());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medication("", Money.of("10.00"), 10, new OtcRule()));
    }

    @Test
    void rejectsNegativeStock() {
        assertThrows(IllegalArgumentException.class,
                () -> new Medication("Paracetamol", Money.of("10.00"), -1, new OtcRule()));
    }

    @Test
    void rejectsNullPrice() {
        assertThrows(NullPointerException.class,
                () -> new Medication("Paracetamol", null, 10, new OtcRule()));
    }

    @Test
    void rejectsNullRule() {
        assertThrows(NullPointerException.class,
                () -> new Medication("Paracetamol", Money.of("10.00"), 10, null));
    }

    @Test
    void reduceStockRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> med().reduceStock(0));
    }

    @Test
    void reduceStockRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> med().reduceStock(-1));
    }

    @Test
    void reduceStockThrowsWhenExceedsStock() {
        Medication m = med();
        assertThrows(InsufficientStockException.class, () -> m.reduceStock(11));
        assertEquals(10, m.getStock());
    }

    @Test
    void reduceStockExactlyAllSucceeds() {
        Medication m = med();
        m.reduceStock(10);
        assertEquals(0, m.getStock());
    }

    @Test
    void increaseStockRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> med().increaseStock(0));
    }

    @Test
    void increaseStockOverflowThrows() {
        Medication m = med();
        assertThrows(ArithmeticException.class,
                () -> m.increaseStock(Integer.MAX_VALUE));
    }
}
