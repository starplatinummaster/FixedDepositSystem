package com.DigitalFixedDepositSystem.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FixedDepositBookingRequest(
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "1000.00", message = "Minimum FD amount is 1000")
        @DecimalMax(value = "1000000.00", message = "Maximum FD amount is 1,000,000")
        BigDecimal amount,

        @NotNull(message = "Tenure cannot be null")
        @Min(value = 1, message = "Tenure must be at least 1 month")
        @Max(value = 120, message = "Tenure cannot exceed 120 months (10 years)")
        Integer tenureMonths
) {}


