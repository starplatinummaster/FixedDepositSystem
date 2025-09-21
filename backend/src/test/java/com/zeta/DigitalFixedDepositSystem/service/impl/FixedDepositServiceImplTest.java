package com.zeta.DigitalFixedDepositSystem.service.impl;

import com.zeta.DigitalFixedDepositSystem.config.AllowedFixedDepositSchemes;
import com.zeta.DigitalFixedDepositSystem.dto.FDBreakRequest;
import com.zeta.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.zeta.DigitalFixedDepositSystem.exception.FDCantBeBrokenException;
import com.zeta.DigitalFixedDepositSystem.exception.ResourceNotFoundException;
import com.zeta.DigitalFixedDepositSystem.model.FDBreak;
import com.zeta.DigitalFixedDepositSystem.model.FixedDeposit;
import com.zeta.DigitalFixedDepositSystem.model.User;
import com.zeta.DigitalFixedDepositSystem.model.enums.FDBreakStatus;
import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import com.zeta.DigitalFixedDepositSystem.repository.FDBreakRepository;
import com.zeta.DigitalFixedDepositSystem.repository.FixedDepositRepository;
import com.zeta.DigitalFixedDepositSystem.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FixedDepositServiceImplTest {

    private static final int PERCENTAGE_DIVISOR = 100;
    private static final int MONTHS_IN_QUARTER = 3;
    private static final int INTEREST_CALCULATION_SCALE = 10;

    @Mock
    private FDBreakRepository fdBreakRepository;

    @Mock
    private FixedDepositRepository fdRepository;

    @Mock
    private AllowedFixedDepositSchemes allowedFixedDepositSchemes;

    @Mock
    private Executor executor;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FixedDepositServiceImpl fdBreakService;

    private FixedDeposit fd;
    private UUID fdId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executor).execute(any());

        when(authService.getCurrentUser())
                .thenReturn(CompletableFuture.completedFuture(
                        new UserProfileResponse(
                                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                                "Test User",
                                "test@example.com",
                                "USER",
                                LocalDateTime.of(2025, 1, 1, 0, 0)
                        )
                ));

        fdId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        User user = User.builder().id(userId).build();

        fd = FixedDeposit.builder()
                .id(fdId)
                .user(user)
                .amount(BigDecimal.valueOf(100000))
                .interestRate(BigDecimal.valueOf(6.0)) // Set effective rate here
                .tenureMonths(12)
                .startDate(LocalDate.now().minusDays(30))
                .maturityDate(LocalDate.now().plusMonths(11))
                .status(FixedDepositStatus.ACTIVE)
                .build();

        when(fdRepository.findById(fdId)).thenReturn(Optional.of(fd));
    }

    @Test
    void previewBreak_valid_returnsFDBreak() {
        LocalDate breakDate = LocalDate.now();
        int elapsedDays = (int) ChronoUnit.DAYS.between(fd.getStartDate(), breakDate);

        when(allowedFixedDepositSchemes.getRateForTenure(elapsedDays))
                .thenReturn(Optional.of(fd.getInterestRate()));

        BigDecimal interestEarned = calculateInterest(fd, breakDate);
        BigDecimal penalty = interestEarned.multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedPayout = fd.getAmount().add(interestEarned).subtract(penalty).setScale(2, RoundingMode.HALF_UP);

        FDBreak result = fdBreakService.previewBreak(fdId)
                .toCompletableFuture()
                .join();

        assertEquals(expectedPayout.setScale(2, RoundingMode.HALF_UP), result.getComputedPayout().setScale(2, RoundingMode.HALF_UP));
        assertEquals(interestEarned.setScale(2, RoundingMode.HALF_UP), result.getInterestEarned().setScale(2, RoundingMode.HALF_UP));
        assertEquals(penalty.setScale(2, RoundingMode.HALF_UP), result.getPenaltyAmount().setScale(2, RoundingMode.HALF_UP));

    }

    @Test
    void previewBreak_onMaturityDate_throwsFDCantBeBrokenException() {
        fd.setStartDate(LocalDate.now().minusMonths(12));
        fd.setMaturityDate(LocalDate.now());

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fdBreakService.previewBreak(fdId)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof FDCantBeBrokenException);
    }

    @Test
    void executeBreak_successful() {
        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Need money");

        int elapsedDays = (int) (LocalDate.now().toEpochDay() - fd.getStartDate().toEpochDay());

        when(allowedFixedDepositSchemes.getRateForTenure(elapsedDays))
                .thenReturn(Optional.of(fd.getInterestRate()));

        FDBreak result = fdBreakService.executeBreak(fdId, request)
                .toCompletableFuture()
                .join();

        assertEquals(FDBreakStatus.EXECUTED, result.getStatus());
        assertEquals(fd.getId(), result.getFixedDeposit().getId());
        assertEquals(FixedDepositStatus.BROKEN, fd.getStatus());

        verify(fdBreakRepository, times(1)).save(any(FDBreak.class));
        verify(fdRepository, times(1)).save(fd);
    }

    @Test
    void executeBreak_alreadyBrokenFD_throwsException() {
        fd.setStatus(FixedDepositStatus.BROKEN);

        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Any reason");

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fdBreakService.executeBreak(fdId, request)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof FDCantBeBrokenException);
    }

    @Test
    void executeBreak_maturedFD_throwsException() {
        fd.setStatus(FixedDepositStatus.MATURED);

        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Another reason");

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fdBreakService.executeBreak(fdId, request)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof FDCantBeBrokenException);
    }

    @Test
    void executeBreak_fdNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(fdRepository.findById(unknownId)).thenReturn(Optional.empty());

        FDBreakRequest request = new FDBreakRequest();
        request.setReason("Test");

        CompletionException ex = assertThrows(CompletionException.class,
                () -> fdBreakService.executeBreak(unknownId, request)
                        .toCompletableFuture()
                        .join());

        assertTrue(ex.getCause() instanceof ResourceNotFoundException);
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
            BigDecimal quarterlyRate = annualRateDecimal.divide(BigDecimal.valueOf(4), INTEREST_CALCULATION_SCALE, RoundingMode.HALF_UP);
            BigDecimal rateFactor = BigDecimal.ONE.add(quarterlyRate);
            BigDecimal compoundedAmount = fixedDeposit.getAmount().multiply(rateFactor.pow((int) numberOfCompoundingPeriods));
            BigDecimal interest = compoundedAmount.subtract(fixedDeposit.getAmount());
            return interest.setScale(2, RoundingMode.HALF_UP);
        }
    }

    private BigDecimal calculateSimpleInterestForPartialPeriod(FixedDeposit fd, BigDecimal annualRateDecimal, LocalDate endDate) {
        long daysElapsed = ChronoUnit.DAYS.between(fd.getStartDate(), endDate);
        BigDecimal dailyRate = annualRateDecimal.divide(BigDecimal.valueOf(365), INTEREST_CALCULATION_SCALE, RoundingMode.HALF_UP);
        return fd.getAmount()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysElapsed))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
