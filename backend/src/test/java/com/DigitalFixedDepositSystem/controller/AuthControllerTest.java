package com.DigitalFixedDepositSystem.controller;

import com.DigitalFixedDepositSystem.dto.AuthRequest;
import com.DigitalFixedDepositSystem.dto.AuthResponse;
import com.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.DigitalFixedDepositSystem.exception.InvalidCredentialsException;
import com.DigitalFixedDepositSystem.exception.UserAlreadyExistsException;
import com.DigitalFixedDepositSystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.sasl.AuthenticationException;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    void testRegister() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        RegisterRequest req = new RegisterRequest("test@example.com", "secret123", "John Doe");

        var response = authController.register(req).toCompletableFuture().get();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User registered successfully", response.getBody());
        verify(authService, times(1)).register(req);
    }

    @Test
    void testLogin() throws Exception {
        AuthResponse mockResponse = new AuthResponse("jwt-token");
        when(authService.login(any(AuthRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        AuthRequest req = new AuthRequest("test@example.com", "secret123");

        var response = authController.login(req).toCompletableFuture().get();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService, times(1)).login(req);
    }

    @Test
    void testGetCurrentUser() throws Exception {
        UUID id=UUID.randomUUID();
        UserProfileResponse profile = new UserProfileResponse(
                id,
                "John Doe",
                "test@example.com",
                "USER",
                LocalDateTime.of(2025, 1, 1, 12, 0)
        );

        when(authService.getCurrentUser())
                .thenReturn(CompletableFuture.completedFuture(profile));

        var response = authController.getCurrentUser().toCompletableFuture().get();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(id,response.getBody().id());
        assertEquals("John Doe", response.getBody().name());
        assertEquals("test@example.com", response.getBody().email());
        assertEquals("USER", response.getBody().role());
        verify(authService, times(1)).getCurrentUser();
    }
    @Test
    void testRegister_UserAlreadyExists() {
        RegisterRequest req = new RegisterRequest("test@example.com", "secret123", "John Doe");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new UserAlreadyExistsException("User exists")));

        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> authController.register(req).toCompletableFuture().get());

        assertTrue(ex.getCause() instanceof UserAlreadyExistsException);
        verify(authService, times(1)).register(req);
    }

    @Test
    void testLogin_InvalidCredentials() {
        AuthRequest req = new AuthRequest("test@example.com", "wrongpass");
        when(authService.login(any(AuthRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new InvalidCredentialsException("Bad creds")));

        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> authController.login(req).toCompletableFuture().get());

        assertInstanceOf(InvalidCredentialsException.class, ex.getCause());
        verify(authService, times(1)).login(req);
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        when(authService.getCurrentUser())
                .thenReturn(CompletableFuture.failedFuture(new AuthenticationException("Not logged in")));

        ExecutionException ex = assertThrows(ExecutionException.class,
                () -> authController.getCurrentUser().toCompletableFuture().get());

        assertInstanceOf(AuthenticationException.class, ex.getCause());
        verify(authService, times(1)).getCurrentUser();
    }
}