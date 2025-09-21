package com.DigitalFixedDepositSystem.utils;

import com.DigitalFixedDepositSystem.dto.FDBreakResponse;
import com.DigitalFixedDepositSystem.model.FDBreak;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FDBreakMapper {

    public FDBreakResponse toDTO(FDBreak fdBreak) {
        return FDBreakResponse.builder()
                .fdId(fdBreak.getFixedDeposit().getId())
                .originalAmount(fdBreak.getFixedDeposit().getAmount())
                .interestEarned(fdBreak.getInterestEarned())
                .interestLost(fdBreak.getInterestLost())
                .penaltyAmount(fdBreak.getPenaltyAmount())
                .computedPayout(fdBreak.getComputedPayout())
                .breakDate(fdBreak.getBreakDate())
                .build();
    }
}
