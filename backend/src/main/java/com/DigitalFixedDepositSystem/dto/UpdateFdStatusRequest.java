package com.DigitalFixedDepositSystem.dto;

import com.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateFdStatusRequest(@NotNull FixedDepositStatus status) { }
