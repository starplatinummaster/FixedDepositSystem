package com.zeta.DigitalFixedDepositSystem.dto;

import com.zeta.DigitalFixedDepositSystem.model.FixedDeposit;
import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FixedDepositResponse(
        UUID id,
        BigDecimal amount,
        BigDecimal interestRate,
        Integer tenureMonths,
        LocalDate startDate,
        LocalDate maturityDate,
        FixedDepositStatus status
) {
    public static FixedDepositResponse fromEntity(FixedDeposit fixedDeposit) {
        return new FixedDepositResponse(
                fixedDeposit.getId(),
                fixedDeposit.getAmount(),
                fixedDeposit.getInterestRate(),
                fixedDeposit.getTenureMonths(),
                fixedDeposit.getStartDate(),
                fixedDeposit.getMaturityDate(),
                fixedDeposit.getStatus()
        );
    }
}
