package com.DigitalFixedDepositSystem.dto;

import com.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record FixedDepositDetailsResponse(
        UUID id,
        BigDecimal amountInvested,
        BigDecimal interestRate,
        Integer tenureMonths,
        LocalDate startDate,
        LocalDate maturityDate,
        BigDecimal progress,
        BigDecimal interestAccrued,
        BigDecimal maturityValue,
        FixedDepositStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
