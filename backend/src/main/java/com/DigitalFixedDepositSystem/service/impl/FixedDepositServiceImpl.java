package com.DigitalFixedDepositSystem.service.impl;

import com.DigitalFixedDepositSystem.config.AllowedFixedDepositSchemes;
import com.DigitalFixedDepositSystem.config.FixedDepositRateConfig;
import com.DigitalFixedDepositSystem.dto.*;
import com.zeta.DigitalFixedDepositSystem.dto.*;
import com.DigitalFixedDepositSystem.exception.FDCantBeBrokenException;
import com.DigitalFixedDepositSystem.exception.FixedDepositNotFoundException;
import com.DigitalFixedDepositSystem.exception.InvalidBreakDateException;
import com.DigitalFixedDepositSystem.exception.ResourceNotFoundException;
import com.DigitalFixedDepositSystem.model.FDBreak;
import com.DigitalFixedDepositSystem.model.FixedDeposit;
import com.DigitalFixedDepositSystem.model.User;
import com.DigitalFixedDepositSystem.model.enums.FDBreakStatus;
import com.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import com.DigitalFixedDepositSystem.repository.FDBreakRepository;
import com.DigitalFixedDepositSystem.repository.FixedDepositRepository;
import com.DigitalFixedDepositSystem.service.AuthService;
import com.DigitalFixedDepositSystem.service.FixedDepositService;
import com.DigitalFixedDepositSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.DigitalFixedDepositSystem.utils.AppConstants.ErrorMessage.*;
import static com.DigitalFixedDepositSystem.utils.AppConstants.FixedDepositCalculation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedDepositServiceImpl implements FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final AllowedFixedDepositSchemes allowedFixedDepositSchemes;
    private final UserService userService;
    private final AuthService authService;
    private final Executor asyncExecutor;
    private final FDBreakRepository fdBreakRepository;
    private final FixedDepositRateConfig fixedDepositRateConfig;

    @Override
    public CompletionStage<FixedDepositResponse> createFixedDeposit(FixedDepositBookingRequest request) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performCreateFixedDeposit(user.id(), request), asyncExecutor);
    }

    @Override
    public CompletionStage<InterestResponse> getAccruedInterest(UUID fixedDepositId) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> calculateAndWrapInterest(fixedDepositId, user.email()), asyncExecutor);
    }

    @Override
    public CompletionStage<FixedDepositResponse> updateStatus(UUID fixedDepositId, UpdateFdStatusRequest request) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> updateFdStatus(fixedDepositId, user.email(), request), asyncExecutor);
    }

    @Override
    public CompletionStage<List<FixedDepositResponse>> getUserActiveFixedDeposits() {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetUserActiveFixedDeposits(user.id()), asyncExecutor);
    }

    @Override
    public CompletionStage<List<FixedDepositDetailsResponse>> getUserFixedDeposits() {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> performGetUserFixedDeposits(user.id()), asyncExecutor);
    }

    @Transactional
    protected FixedDepositResponse performCreateFixedDeposit(UUID userId, FixedDepositBookingRequest request) {
        User user = userService.findUserById(userId);
        FixedDeposit newFixedDeposit = buildNewFixedDeposit(user, request);
        FixedDeposit savedFixedDeposit = fixedDepositRepository.save(newFixedDeposit);
        return FixedDepositResponse.fromEntity(savedFixedDeposit);
    }

    private FixedDeposit buildNewFixedDeposit(User user, FixedDepositBookingRequest request) {
        // Fetch rate and canonical month using the key from request
        RateAndMonth rateAndMonth = fixedDepositRateConfig.getRateAndMonthForTenure(request.tenureMonths())
                .orElseThrow(() -> new IllegalArgumentException("No rate configured for this tenure"));

        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(rateAndMonth.getMonth());

        return FixedDeposit.builder()
                .user(user)
                .amount(request.amount())
                .interestRate(rateAndMonth.getRate())
                .tenureMonths(rateAndMonth.getMonth()) // store canonical month
                .startDate(startDate)
                .maturityDate(maturityDate)
                .status(FixedDepositStatus.ACTIVE)
                .build();
    }

    @Transactional(readOnly = true)
    protected List<FixedDepositResponse> performGetUserActiveFixedDeposits(UUID userId) {
        List<FixedDeposit> fixedDeposits = fixedDepositRepository.findByUserIdAndStatus(userId, FixedDepositStatus.ACTIVE);
        return fixedDeposits.stream()
                .map(FixedDepositResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    protected List<FixedDepositDetailsResponse> performGetUserFixedDeposits(UUID userId) {
        List<FixedDeposit> fixedDeposits = fixedDepositRepository.findByUserId(userId);
        return fixedDeposits.stream()
                .map(this::mapToFixedDepositDetailsResponse)
                .collect(Collectors.toList());
    }

    protected InterestResponse calculateAndWrapInterest(UUID fixedDepositId, String email) {
        FixedDeposit fixedDeposit = findFdByIdAndUserEmail(fixedDepositId, email);
        BigDecimal interest = calculateInterest(fixedDeposit);
        return new InterestResponse(interest);
    }

    @Transactional
    protected FixedDepositResponse updateFdStatus(UUID fixedDepositId, String email, UpdateFdStatusRequest request) {
        FixedDeposit fixedDeposit = findFdByIdAndUserEmail(fixedDepositId, email);
        fixedDeposit.setStatus(request.status());
        FixedDeposit updatedFd = fixedDepositRepository.save(fixedDeposit);
        return FixedDepositResponse.fromEntity(updatedFd);
    }

    private FixedDepositDetailsResponse mapToFixedDepositDetailsResponse(FixedDeposit fd) {
        BigDecimal interestAccrued = calculateInterest(fd);
        BigDecimal progress = calculateProgress(fd);
        BigDecimal maturityValue = calculateMaturityValue(fd);

        return FixedDepositDetailsResponse.builder()
                .id(fd.getId())
                .amountInvested(fd.getAmount())
                .interestRate(fd.getInterestRate())
                .tenureMonths(fd.getTenureMonths())
                .startDate(fd.getStartDate())
                .maturityDate(fd.getMaturityDate())
                .status(fd.getStatus())
                .createdAt(fd.getCreatedAt())
                .updatedAt(fd.getUpdatedAt())
                .interestAccrued(interestAccrued)
                .maturityValue(maturityValue)
                .progress(progress)
                .build();
    }

    private BigDecimal calculateMaturityValue(FixedDeposit fixedDeposit) {
        BigDecimal totalInterest = calculateInterest(fixedDeposit, fixedDeposit.getMaturityDate());
        return fixedDeposit.getAmount().add(totalInterest).setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInterest(FixedDeposit fixedDeposit, LocalDate calculationEndDate) {
        if (calculationEndDate.isBefore(fixedDeposit.getStartDate()) || calculationEndDate.isEqual(fixedDeposit.getStartDate())) {
            return BigDecimal.ZERO;
        }

        BigDecimal annualRateDecimal = fixedDeposit.getInterestRate()
                .divide(BigDecimal.valueOf(PERCENTAGE_DIVISOR), INTEREST_CALCULATION_SCALE, RoundingMode.HALF_UP);

        long totalMonthsElapsed = ChronoUnit.MONTHS.between(fixedDeposit.getStartDate(), calculationEndDate);
        long numberOfCompoundingPeriods = totalMonthsElapsed / MONTHS_IN_QUARTER;

        if (numberOfCompoundingPeriods <= 0) {
            return calculateSimpleInterestForPartialPeriod(fixedDeposit, annualRateDecimal, calculationEndDate);
        } else {
            return calculateCompoundInterestForFullPeriods(fixedDeposit, annualRateDecimal, numberOfCompoundingPeriods);
        }
    }

    private BigDecimal calculateInterest(FixedDeposit fixedDeposit) {
        return calculateInterest(fixedDeposit, LocalDate.now());
    }

    private BigDecimal calculateProgress(FixedDeposit fixedDeposit) {
        LocalDate startDate = fixedDeposit.getStartDate();
        LocalDate maturityDate = fixedDeposit.getMaturityDate();
        LocalDate currentDate = LocalDate.now();
        // If the FD has matured or is maturing today, progress is 100%.
        // This also handles the case where startDate equals maturityDate.
        if (!currentDate.isBefore(maturityDate)) {
            return BigDecimal.valueOf(100);
        }

        // If the FD has not started yet, progress is 0%.
        if (currentDate.isBefore(startDate)) {
            return BigDecimal.ZERO;
        }

        // This check prevents division by zero if for some reason startDate and maturityDate are the same
        // and the above checks didn't catch it (e.g., if currentDate is before a future start/maturity date).
        if (startDate.isEqual(maturityDate)) {
            return BigDecimal.ZERO;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, maturityDate);
        long elapsedDays = ChronoUnit.DAYS.between(startDate, currentDate);

        return BigDecimal.valueOf(elapsedDays)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
    }

    private FixedDeposit findFdByIdAndUserEmail(UUID fixedDepositId, String email) {
        return fixedDepositRepository.findByIdAndUserEmail(fixedDepositId, email)
                .orElseThrow(() -> new FixedDepositNotFoundException(FIXED_DEPOSIT_NOT_FOUND + fixedDepositId));
    }

    private BigDecimal calculateSimpleInterestForPartialPeriod(FixedDeposit fixedDeposit, BigDecimal annualRateDecimal, LocalDate currentDate) {
        long daysElapsed = ChronoUnit.DAYS.between(fixedDeposit.getStartDate(), currentDate);
        BigDecimal dailyRate = annualRateDecimal.divide(BigDecimal.valueOf(365),
                INTEREST_CALCULATION_SCALE, RoundingMode.HALF_UP);

        return fixedDeposit.getAmount()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysElapsed))
                .setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCompoundInterestForFullPeriods(FixedDeposit fixedDeposit, BigDecimal annualRateDecimal, long numberOfCompoundingPeriods) {
        BigDecimal principal = fixedDeposit.getAmount();
        BigDecimal quarterlyRate = annualRateDecimal.divide(BigDecimal.valueOf(COMPOUNDING_FREQUENCY_QUARTERLY),
                INTEREST_CALCULATION_SCALE, RoundingMode.HALF_UP);

        BigDecimal rateFactor = BigDecimal.ONE.add(quarterlyRate);
        BigDecimal compoundedAmount = principal.multiply(rateFactor.pow((int) numberOfCompoundingPeriods));

        BigDecimal totalInterest = compoundedAmount.subtract(principal);

        return totalInterest.setScale(MONETARY_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public CompletionStage<FDBreak> previewBreak(UUID fdId) {
        return authService.getCurrentUser()
                .thenApplyAsync(user -> calculatePreview(fdId), asyncExecutor);    }

    @Transactional
    @Override
    public CompletionStage<FDBreak> executeBreak(UUID fdId, FDBreakRequest fdBreakRequest) {
        return authService.getCurrentUser()
                .thenApplyAsync(userProfileResponse -> performExecution(fdId, fdBreakRequest), asyncExecutor);
    }

    @Transactional(readOnly = true)
    protected FDBreak calculatePreview(UUID fdId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("FD not found"));

        if (fd.getStatus() != FixedDepositStatus.ACTIVE) {
            log.warn("FD {} is already {}. Cannot preview break again.", fdId, fd.getStatus());
            throw new FDCantBeBrokenException("FD cannot be previewed to break because it is already " + fd.getStatus());
        }

        LocalDate breakDate = LocalDate.now();
        if (breakDate.isBefore(fd.getStartDate())) {
            throw new InvalidBreakDateException("Break date " + breakDate + " cannot be before FD start date " + fd.getStartDate());
        } else if (breakDate.isAfter(fd.getMaturityDate())) {
            throw new InvalidBreakDateException("Break date " + breakDate + " cannot be after FD maturity date " + fd.getMaturityDate());
        }

        if(breakDate.equals(fd.getMaturityDate())){
            log.warn("FD {} Cannot be broken on maturity date {}.", fdId, fd.getMaturityDate());
            throw new FDCantBeBrokenException("FD cannot be broken on maturity date " + fd.getMaturityDate());
        }
        int elapsedDays = (int) ChronoUnit.DAYS.between(fd.getStartDate(), breakDate);
        BigDecimal effectiveRate = calculateEffectiveRate(elapsedDays);

        BigDecimal interestEarned = calculateInterest(fd, breakDate);
        BigDecimal penalty = computePenalty(breakDate, fd.getMaturityDate(), interestEarned);
        BigDecimal expectedInterest = calculateInterest(fd, fd.getMaturityDate());
        BigDecimal interestLost = expectedInterest.subtract(interestEarned);

        FDBreak fdBreak = FDBreak.builder()
                .fixedDeposit(fd)
                .userId(fd.getUser().getId())
                .requestedAt(java.time.Instant.now())
                .computedPayout(fd.getAmount().add(interestEarned).subtract(penalty).setScale(2, RoundingMode.HALF_UP))
                .interestEarned(interestEarned)
                .interestLost(interestLost)
                .penaltyAmount(penalty)
                .effectiveRate(effectiveRate)
                .breakDate(breakDate)
                .status(FDBreakStatus.PREVIEWED)
                .build();

        log.info("Preview FD Break for FD ID {}: elapsedDays={}, effectiveRate={}, payout={}",
                fd.getId(), elapsedDays, effectiveRate, fdBreak.getComputedPayout());

        return fdBreak;
    }


    @Transactional
    protected FDBreak performExecution(UUID fdId, FDBreakRequest fdBreakRequest) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("FD not found"));

        if (fd.getStatus() != FixedDepositStatus.ACTIVE) {
            log.warn("FD {} is already {}. Cannot break again.", fdId, fd.getStatus());
            throw new FDCantBeBrokenException("FD cannot be broken because it is already " + fd.getStatus());
        }
        String reason = fdBreakRequest.getReason();

        FDBreak fdBreak = calculatePreview(fdId);
        fdBreak.setStatus(FDBreakStatus.EXECUTED);

        fdBreakRepository.save(fdBreak);
        fd.setStatus(FixedDepositStatus.BROKEN);
        fixedDepositRepository.save(fd);

        log.info("Executed FD Break for FD ID {}: payout={}, reason={}", fd.getId(), fdBreak.getComputedPayout(), reason);

        return fdBreak;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateEffectiveRate( int elapsedDays) {
        return allowedFixedDepositSchemes.getRateForTenure(elapsedDays).orElse(BigDecimal.ZERO);

    }

    private BigDecimal computePenalty(LocalDate breakDate, LocalDate maturityDate, BigDecimal interestEarned) {
        if (breakDate.isBefore(maturityDate)) {
            return interestEarned.multiply(BigDecimal.valueOf(0.01));
        }
        return BigDecimal.ZERO;
    }
}