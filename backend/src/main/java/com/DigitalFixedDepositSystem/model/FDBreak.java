package com.DigitalFixedDepositSystem.model;

import com.DigitalFixedDepositSystem.model.enums.FDBreakStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fd_break")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FDBreak {

    @Id
    @GeneratedValue
    @Column(name = "fd_break_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "fd_id", referencedColumnName = "id")
    private FixedDeposit fixedDeposit;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "computed_payout", nullable = false, precision = 18, scale = 2)
    private BigDecimal computedPayout;

    @Column(name = "interest_earned", nullable = false, precision = 18, scale = 2)
    private BigDecimal interestEarned;

    @Column(name = "interest_lost", nullable = false, precision = 18, scale = 2)
    private BigDecimal interestLost;

    @Column(name = "penalty_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "effective_rate", precision = 5, scale = 2)
    private BigDecimal effectiveRate;

    @Column(name = "break_date", nullable = false)
    private LocalDate breakDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private FDBreakStatus status;
}

