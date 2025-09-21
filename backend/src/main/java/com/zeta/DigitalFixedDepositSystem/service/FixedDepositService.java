package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.model.FDBreak;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface FixedDepositService {
    CompletionStage<InterestResponse> getAccruedInterest(UUID fdId);
    CompletionStage<FixedDepositResponse> updateStatus(UUID fdId, UpdateFdStatusRequest request);
    CompletionStage<List<FixedDepositResponse>> getUserActiveFixedDeposits();
    CompletionStage<FixedDepositResponse> createFixedDeposit(FixedDepositBookingRequest request);
    CompletionStage<List<FixedDepositDetailsResponse>> getUserFixedDeposits();
    CompletionStage<FDBreak> previewBreak(UUID fdId);
    CompletionStage<FDBreak> executeBreak(UUID fdId, FDBreakRequest fdBreakRequest);
    BigDecimal calculateEffectiveRate(int elapsedDays);
}
