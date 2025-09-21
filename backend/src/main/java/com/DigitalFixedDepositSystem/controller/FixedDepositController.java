package com.DigitalFixedDepositSystem.controller;

import com.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.DigitalFixedDepositSystem.service.FixedDepositService;
import com.DigitalFixedDepositSystem.utils.FDBreakMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/fd")
@RequiredArgsConstructor
@Slf4j
public class FixedDepositController {

    private final FixedDepositService fixedDepositService;

    @PostMapping("")
    public CompletionStage<ResponseEntity<FixedDepositResponse>> createFixedDeposit(@Valid @RequestBody FixedDepositBookingRequest request) {
        return fixedDepositService.createFixedDeposit(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("")
    public CompletionStage<ResponseEntity<List<FixedDepositDetailsResponse>>> getUserFixedDeposits() {
        return fixedDepositService.getUserFixedDeposits()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/active")
    public CompletionStage<ResponseEntity<List<FixedDepositResponse>>> getUserActiveFixedDeposits() {
        return fixedDepositService.getUserActiveFixedDeposits()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{fdId}/interest")
    public CompletionStage<ResponseEntity<InterestResponse>> getAccruedInterest(@PathVariable UUID fdId) {
        return fixedDepositService.getAccruedInterest(fdId)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{fdId}/status")
    public CompletionStage<ResponseEntity<FixedDepositResponse>> updateStatus(
            @PathVariable UUID fdId,
            @Valid @RequestBody UpdateFdStatusRequest request) {
        return fixedDepositService.updateStatus(fdId, request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{fdId}/break-preview")
    public CompletionStage<ResponseEntity<FDBreakResponse>> getBreakPreview(@PathVariable UUID fdId) {
        log.info("API Request - Preview FD break for fdId: {}", fdId);
        return fixedDepositService.previewBreak(fdId)
                .thenApply(FDBreakMapper::toDTO)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/{fdId}/break")
    public CompletionStage<ResponseEntity<FDBreakResponse>> executeBreak(
            @PathVariable UUID fdId,
            @RequestBody FDBreakRequest request
    ) {
        log.info("API Request - Execute FD break for fdId: {}", fdId);
        return fixedDepositService.executeBreak(fdId, request)
                .thenApply(FDBreakMapper::toDTO)
                .thenApply(ResponseEntity::ok);
    }
}
