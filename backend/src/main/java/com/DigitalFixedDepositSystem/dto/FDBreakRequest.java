package com.DigitalFixedDepositSystem.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FDBreakRequest {
    private String reason;
}
