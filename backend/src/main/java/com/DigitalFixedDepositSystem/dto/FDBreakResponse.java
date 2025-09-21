package com.DigitalFixedDepositSystem.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FDBreakResponse {
    private UUID fdId;
    private BigDecimal originalAmount;
    private BigDecimal interestEarned;
    private BigDecimal interestLost;
    private BigDecimal penaltyAmount;
    private BigDecimal computedPayout;
    private LocalDate breakDate;
}