package com.zeta.DigitalFixedDepositSystem.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RateAndMonth {
    private final BigDecimal rate;
    private final int month;

    public RateAndMonth(BigDecimal rate, int month) {
        this.rate = rate;
        this.month = month;
    }

}
