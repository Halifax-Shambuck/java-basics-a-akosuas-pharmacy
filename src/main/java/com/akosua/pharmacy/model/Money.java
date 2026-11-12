package com.akosua.pharmacy.model;

import java.math.BigDecimal;

// A value type for monetary amounts. BigDecimal gives exact decimal arithmetic,
// avoiding the binary rounding errors that double produces for fractions like 0.95.
// All amounts are non-negative; refunds are not modelled here.
public record Money(BigDecimal amount) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        // signum() returns -1 for negative values
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        // stripTrailingZeros gives one canonical form so 2.50 and 2.00 compare equal
        amount = amount.stripTrailingZeros();
    }

    public static final Money ZERO = Money.of("0");

    // Always construct from a String, never from a double,
    // to avoid carrying double's binary rounding error into BigDecimal.
    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    // Returns the line total: price multiplied by a whole quantity.
    public Money multiplyBy(int qty) {
        return new Money(amount.multiply(BigDecimal.valueOf(qty)));
    }

    // Returns a discounted amount: total multiplied by a decimal factor such as
    // 0.95.
    public Money multiplyBy(BigDecimal factor) {
        return new Money(amount.multiply(factor));
    }

    // stripTrailingZeros removes trailing zeros, but can produce scale 0 for whole
    // numbers like 25, which toPlainString prints as "25" not "25.0".
    // setScale(1) raises it to exactly one decimal place, matching the original
    // output.
    // Adding a decimal place is always exact so no rounding mode is needed.

    @Override
    public String toString() {
        java.math.BigDecimal normalised = amount.stripTrailingZeros();
        if (normalised.scale() < 1) {
            normalised = normalised.setScale(1);
        }
        return normalised.toPlainString();

    }
}