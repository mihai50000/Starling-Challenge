package com.starling.starlingroundup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CurrencyAndAmount {
    private String currency;
    private long minorUnits;

    public CurrencyAndAmount(CurrencyAndAmount other) {
        this.currency = other.currency;
        this.minorUnits = other.minorUnits;
    }

    public CurrencyAndAmount add(CurrencyAndAmount currencyAndAmount) {
        if (!currency.equals(currencyAndAmount.currency)) {
            throw new IllegalArgumentException("Can not perform addition on amounts of different types!");
        }

        return new CurrencyAndAmount(currency, minorUnits + currencyAndAmount.minorUnits);
    }

    public static CurrencyAndAmount getEmpty(String currency) {
        return new CurrencyAndAmount(currency, 0);
    }
}
