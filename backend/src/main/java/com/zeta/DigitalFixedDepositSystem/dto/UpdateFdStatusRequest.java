package com.zeta.DigitalFixedDepositSystem.dto;

import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateFdStatusRequest(@NotNull FixedDepositStatus status) { }
