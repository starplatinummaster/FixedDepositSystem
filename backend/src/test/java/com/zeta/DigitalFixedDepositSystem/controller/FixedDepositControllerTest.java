package com.zeta.DigitalFixedDepositSystem.controller;

import com.zeta.DigitalFixedDepositSystem.dto.FDBreakRequest;
import com.zeta.DigitalFixedDepositSystem.dto.FDBreakResponse;
import com.zeta.DigitalFixedDepositSystem.exception.FDCantBeBrokenException;
import com.zeta.DigitalFixedDepositSystem.exception.ResourceNotFoundException;
import com.zeta.DigitalFixedDepositSystem.model.FDBreak;
import com.zeta.DigitalFixedDepositSystem.model.FixedDeposit;
import com.zeta.DigitalFixedDepositSystem.model.User;
import com.zeta.DigitalFixedDepositSystem.model.enums.FDBreakStatus;
import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import com.zeta.DigitalFixedDepositSystem.service.FixedDepositService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class FixedDepositControllerTest {

    @Mock
    private FixedDepositService fixedDepositService;

    @InjectMocks
    private FixedDepositController fixedDepositController;

    private FixedDeposit fd;
    private UUID fdId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        fdId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        User user = User.builder()
                .id(userId)
                .build();

        fd = FixedDeposit.builder()
                .id(fdId)
                .user(user)
                .amount(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(6.25))
                .tenureMonths(12)
                .startDate(LocalDate.of(2025, 1, 1))
                .maturityDate(LocalDate.of(2026, 1, 1))
                .status(FixedDepositStatus.ACTIVE)
                .build();
    }

    @Test
    void getBreakPreview_returnsDTO() {
        FDBreak fdBreak = FDBreak.builder()
                .fixedDeposit(fd)
                .breakDate(LocalDate.now())
                .computedPayout(BigDecimal.valueOf(100256.44))
                .interestEarned(BigDecimal.valueOf(259.04))
                .interestLost(BigDecimal.valueOf(271.78))
                .penaltyAmount(BigDecimal.valueOf(2.59))
                .status(FDBreakStatus.PREVIEWED)
                .build();

        when(fixedDepositService.previewBreak(fdId))
                .thenReturn(CompletableFuture.completedFuture(fdBreak));

        ResponseEntity<FDBreakResponse> response =
                fixedDepositController.getBreakPreview(fdId)
                        .toCompletableFuture()
                        .join();

        FDBreakResponse dto = response.getBody();

        assertEquals(fd.getId(), dto.getFdId());
        assertEquals(BigDecimal.valueOf(100256.44), dto.getComputedPayout());
        assertEquals(LocalDate.now(), dto.getBreakDate());
    }

    @Test
    void executeBreak_returnsDTO() {
        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Personal");

        FDBreak fdBreak = FDBreak.builder()
                .fixedDeposit(fd)
                .breakDate(LocalDate.now())
                .computedPayout(BigDecimal.valueOf(100256.44))
                .interestEarned(BigDecimal.valueOf(259.04))
                .interestLost(BigDecimal.valueOf(271.78))
                .penaltyAmount(BigDecimal.valueOf(2.59))
                .status(FDBreakStatus.EXECUTED)
                .build();

        when(fixedDepositService.executeBreak(fdId, request))
                .thenReturn(CompletableFuture.completedFuture(fdBreak));

        ResponseEntity<FDBreakResponse> response =
                fixedDepositController.executeBreak(fdId, request)
                        .toCompletableFuture()
                        .join();

        FDBreakResponse dto = response.getBody();

        assertEquals(fd.getId(), dto.getFdId());
        assertEquals(BigDecimal.valueOf(100256.44), dto.getComputedPayout());
        assertEquals(LocalDate.now(), dto.getBreakDate());
    }

    @Test
    void executeBreak_onMaturityDate_throwsFDCantBeBrokenException() {
        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Urgent");

        when(fixedDepositService.executeBreak(eq(fdId), any()))
                .thenThrow(new CompletionException(new FDCantBeBrokenException("Cannot break on maturity date")));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fixedDepositController.executeBreak(fdId, request)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof FDCantBeBrokenException);
    }

    @Test
    void executeBreak_fdNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");

        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Personal");

        when(fixedDepositService.executeBreak(eq(unknownId), any()))
                .thenThrow(new CompletionException(new ResourceNotFoundException("FD not found")));

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fixedDepositController.executeBreak(unknownId, request)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
    }
}