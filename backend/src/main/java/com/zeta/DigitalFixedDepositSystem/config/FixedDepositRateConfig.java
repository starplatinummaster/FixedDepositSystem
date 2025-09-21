package com.zeta.DigitalFixedDepositSystem.config;

import com.zeta.DigitalFixedDepositSystem.dto.RateAndMonth;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FixedDepositRateConfig {

    private final Map<Integer, RateAndMonth> tenureToRateMap = new HashMap<>();

    public FixedDepositRateConfig() {
        tenureToRateMap.put(1,   new RateAndMonth(new BigDecimal("3.05"), 1));
        tenureToRateMap.put(2,   new RateAndMonth(new BigDecimal("5.05"), 6));
        tenureToRateMap.put(3,   new RateAndMonth(new BigDecimal("5.80"), 7));
        tenureToRateMap.put(4,   new RateAndMonth(new BigDecimal("6.05"), 12));
        tenureToRateMap.put(5,   new RateAndMonth(new BigDecimal("6.35"), 24));
        tenureToRateMap.put(6,   new RateAndMonth(new BigDecimal("6.45"), 36));
        tenureToRateMap.put(7,   new RateAndMonth(new BigDecimal("6.30"), 60));
        tenureToRateMap.put(8,   new RateAndMonth(new BigDecimal("6.05"), 120));
    }

    public Optional<RateAndMonth> getRateAndMonthForTenure(int tenureKey) {
        return Optional.ofNullable(tenureToRateMap.get(tenureKey));
    }
}