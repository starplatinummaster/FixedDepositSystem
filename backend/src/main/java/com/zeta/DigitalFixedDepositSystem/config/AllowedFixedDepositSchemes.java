package com.zeta.DigitalFixedDepositSystem.config;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

@Component
public class AllowedFixedDepositSchemes {

    // Use a NavigableMap to store rate slabs. Key is the minimum days for the slab.
    private static final NavigableMap<Integer, BigDecimal> RATE_SLABS;

    static {
        NavigableMap<Integer, BigDecimal> slabs = new TreeMap<>();
        slabs.put(7, new BigDecimal("3.05"));    // 7 to 45 days
        slabs.put(46, new BigDecimal("5.05"));   // 46 to 179 days
        slabs.put(180, new BigDecimal("5.80"));  // 180 to 210 days
        slabs.put(211, new BigDecimal("6.05"));  // 211 to 364 days
        slabs.put(365, new BigDecimal("6.25"));  // 365 to 729 days (1-2 years)
        slabs.put(730, new BigDecimal("6.45"));  // 730 to 1094 days (2-3 years)
        slabs.put(1095, new BigDecimal("6.30")); // 1095 to 1824 days (3-5 years)
        slabs.put(1825, new BigDecimal("6.05")); // 1825 to 3650 days (5-10 years)
        RATE_SLABS = Collections.unmodifiableNavigableMap(slabs);
    }

    /**
     * Finds the interest rate for a given tenure in days.
     *
     * @param days The tenure in days. Must be 7 or more.
     * @return An Optional containing the applicable rate, or empty if the tenure is too short.
     */
    public Optional<BigDecimal> getRateForTenure(int days) {
        // Find the rate slab for the given number of days.
        Map.Entry<Integer, BigDecimal> slab = RATE_SLABS.floorEntry(days);
        return Optional.ofNullable(slab).map(Map.Entry::getValue);
    }

    /**
     * Returns an unmodifiable view of all defined rate slabs.
     *
     * @return A map where the key is the minimum tenure in days and the value is the interest rate.
     */
    public Map<Integer, BigDecimal> getAllRateSlabs() {
        return RATE_SLABS;
    }
}