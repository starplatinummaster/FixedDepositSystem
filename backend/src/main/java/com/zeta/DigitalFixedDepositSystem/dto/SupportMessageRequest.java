package com.zeta.DigitalFixedDepositSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupportMessageRequest {
    @NotBlank(message = "Message cannot be empty")
    private String message;
}