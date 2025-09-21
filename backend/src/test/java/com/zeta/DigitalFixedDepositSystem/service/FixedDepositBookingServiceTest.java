package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.config.AllowedFixedDepositSchemes;
import com.zeta.DigitalFixedDepositSystem.config.FixedDepositRateConfig;
import com.zeta.DigitalFixedDepositSystem.dto.FixedDepositBookingRequest;
import com.zeta.DigitalFixedDepositSystem.dto.FixedDepositResponse;
import com.zeta.DigitalFixedDepositSystem.dto.InterestResponse;
import com.zeta.DigitalFixedDepositSystem.dto.UpdateFdStatusRequest;
import com.zeta.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.zeta.DigitalFixedDepositSystem.model.FixedDeposit;
import com.zeta.DigitalFixedDepositSystem.model.User;
import com.zeta.DigitalFixedDepositSystem.model.enums.FixedDepositStatus;
import com.zeta.DigitalFixedDepositSystem.repository.FDBreakRepository;
import com.zeta.DigitalFixedDepositSystem.repository.FixedDepositRepository;
import com.zeta.DigitalFixedDepositSystem.service.impl.FixedDepositServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FixedDepositBookingServiceTest {

    private FixedDepositRepository fdRepo;
    private AllowedFixedDepositSchemes allowedFdSchemes;
    private UserService userService;
    private Executor executor;
    FixedDepositRateConfig fixedDepositRateConfig = new FixedDepositRateConfig();

    private FixedDepositServiceImpl service;

    // Common test data
    private UUID userId;
    private String userEmail;

    @BeforeEach
    void setup() {
        fdRepo = mock(FixedDepositRepository.class);
        FDBreakRepository fdBreakRepository = mock(FDBreakRepository.class);
        allowedFdSchemes = mock(AllowedFixedDepositSchemes.class);
        userService = mock(UserService.class);
        AuthService authService = mock(AuthService.class);
        executor = Executors.newSingleThreadExecutor();

        service = new FixedDepositServiceImpl(fdRepo, allowedFdSchemes, userService, authService, executor, fdBreakRepository, fixedDepositRateConfig);

        // User profile returned by AuthService
        userId = UUID.randomUUID();
        userEmail = "john@example.com";
        String userName = "John Doe";

        var profile = new UserProfileResponse(
                userId, userName, userEmail, "USER", LocalDateTime.now()
        );
        when(authService.getCurrentUser())
                .thenReturn(CompletableFuture.completedFuture(profile));

        // User entity returned by UserService
        var userEntity = User.builder()
                .id(userId)
                .email(userEmail)
                .name(userName)
                .password("secret")
                .role("USER")
                .build();
        when(userService.findUserById(userId)).thenReturn(userEntity);
    }

    @AfterEach
    void tearDown() {
        if (executor instanceof AutoCloseable closeable) {
            try { closeable.close(); } catch (Exception ignored) {}
        }
    }

    @Test
    void bookForUser_withValidRequest_createsFD() throws Exception {
        // Given
        int requestKey = 2; // Example: 2 maps to (5.05, 6) in FixedDepositRateConfig
        var request = new FixedDepositBookingRequest(
                new BigDecimal("5000.00"),
                requestKey
        );

        // Save returns the same entity with an ID set
        when(fdRepo.save(any(FixedDeposit.class))).thenAnswer(inv -> {
            FixedDeposit fd = inv.getArgument(0);
            if (fd.getId() == null) fd.setId(UUID.randomUUID());
            return fd;
        });

        // When
        FixedDepositResponse resp = service.createFixedDeposit(request)
                .toCompletableFuture().get();

        // Then
        assertNotNull(resp);
        assertEquals(new BigDecimal("5000.00"), resp.amount());
        assertEquals(6, resp.tenureMonths()); // canonical month for key 2
        assertEquals(new BigDecimal("5.05"), resp.interestRate()); // rate for key 2
        assertEquals(FixedDepositStatus.ACTIVE, resp.status());

        verify(fdRepo, times(1)).save(any(FixedDeposit.class));
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void getAccruedInterest_startDateToday_returnsZero() throws Exception {
        // Given a fresh FD that starts today => interest must be ZERO by implementation
        UUID fdId = UUID.randomUUID();
        FixedDeposit fd = FixedDeposit.builder()
                .id(fdId)
                .amount(new BigDecimal("10000.00"))
                .interestRate(new BigDecimal("8.00"))
                .tenureMonths(12)
                .startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(12))
                .status(FixedDepositStatus.ACTIVE)
                .build();

        when(fdRepo.findByIdAndUserEmail(eq(fdId), eq(userEmail)))
                .thenReturn(Optional.of(fd));

        // When
        InterestResponse resp = service.getAccruedInterest(fdId)
                .toCompletableFuture().get();

        // Then
        assertNotNull(resp);
        assertEquals(BigDecimal.ZERO, resp.interestAccrued());
        verify(fdRepo, times(1)).findByIdAndUserEmail(fdId, userEmail);
    }

    @Test
    void getUserActiveFixedDeposits_returnsDeposits() throws Exception {
        // Given
        FixedDeposit fd = FixedDeposit.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("5000.00"))
                .interestRate(new BigDecimal("7.25"))
                .tenureMonths(6)
                .startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(6))
                .status(FixedDepositStatus.ACTIVE)
                .build();

        when(fdRepo.findByUserIdAndStatus(userId, FixedDepositStatus.ACTIVE))
                .thenReturn(List.of(fd));

        // When
        var list = service.getUserActiveFixedDeposits()
                .toCompletableFuture().get();

        // Then
        assertEquals(1, list.size());
        assertEquals(new BigDecimal("5000.00"), list.get(0).amount());
        assertEquals(FixedDepositStatus.ACTIVE, list.get(0).status());
        verify(fdRepo, times(1)).findByUserIdAndStatus(userId, FixedDepositStatus.ACTIVE);
    }

    @Test
    void updateStatus_changesStatusAndSaves() throws Exception {
        // Given
        UUID fdId = UUID.randomUUID();
        FixedDeposit fd = FixedDeposit.builder()
                .id(fdId)
                .amount(new BigDecimal("7500.00"))
                .interestRate(new BigDecimal("7.00"))
                .tenureMonths(9)
                .startDate(LocalDate.now().minusMonths(1))
                .maturityDate(LocalDate.now().plusMonths(8))
                .status(FixedDepositStatus.ACTIVE)
                .build();

        when(fdRepo.findByIdAndUserEmail(fdId, userEmail)).thenReturn(Optional.of(fd));
        when(fdRepo.save(any(FixedDeposit.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        var resp = service.updateStatus(fdId, new UpdateFdStatusRequest(FixedDepositStatus.MATURED))
                .toCompletableFuture().get();

        // Then
        assertEquals(FixedDepositStatus.MATURED, resp.status());
        verify(fdRepo, times(1)).findByIdAndUserEmail(fdId, userEmail);
        verify(fdRepo, times(1)).save(any(FixedDeposit.class));
    }
}