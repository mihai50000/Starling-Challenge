package com.starling.starlingroundup.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class RoundUpWindow extends CurrencyAndAmount {
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public RoundUpWindow(CurrencyAndAmount currencyAndAmount, ZonedDateTime startTime, ZonedDateTime endTime) {
        super(currencyAndAmount);

        this.startTime = startTime;
        this.endTime = endTime;
    }
}
