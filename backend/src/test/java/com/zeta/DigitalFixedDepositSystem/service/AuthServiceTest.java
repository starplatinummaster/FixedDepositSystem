package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.dto.AuthRequest;
import com.zeta.DigitalFixedDepositSystem.dto.AuthResponse;
import com.zeta.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.zeta.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.zeta.DigitalFixedDepositSystem.exception.InvalidCredentialsException;
import com.zeta.DigitalFixedDepositSystem.exception.UserAlreadyExistsException;
import com.zeta.DigitalFixedDepositSystem.model.User;
import com.zeta.DigitalFixedDepositSystem.repository.UserRepository;
import com.zeta.DigitalFixedDepositSystem.security.JWTUtil;
import com.zeta.DigitalFixedDepositSystem.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JWTUtil jwtUtil;
    @Mock private Authentication authentication;
    @Mock private UserDetails userDetails;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Executor executor = Executors.newSingleThreadExecutor();
        authService = new AuthServiceImpl(userRepository, passwordEncoder, authenticationManager, jwtUtil, executor);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("john@example.com", "pass123", "John Doe");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID()); 
            user.setRole("USER");
            return user;
        });

        authService.register(request).toCompletableFuture().get();
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void testRegisterFailsWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("john@example.com", "pass123", "John Doe");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        CompletionStage<Void> stage = authService.register(request);

        ExecutionException ex = assertThrows(ExecutionException.class, () -> stage.toCompletableFuture().get());
        assertInstanceOf(UserAlreadyExistsException.class, ex.getCause());
    }

    @Test
    void testLoginSuccess() throws Exception {
        AuthRequest request = new AuthRequest("john@example.com", "pass123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request).toCompletableFuture().get();

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void testGetCurrentUserFailsWhenNotAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(null);

        CompletionStage<UserProfileResponse> stage = authService.getCurrentUser();

        ExecutionException ex = assertThrows(ExecutionException.class, () -> stage.toCompletableFuture().get());
        assertInstanceOf(InvalidCredentialsException.class, ex.getCause());
    }

    @Test
    void testGetCurrentUserFailsWhenUserNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("missing@example.com");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        CompletionStage<UserProfileResponse> stage = authService.getCurrentUser();

        ExecutionException ex = assertThrows(ExecutionException.class, () -> stage.toCompletableFuture().get());
        assertInstanceOf(InvalidCredentialsException.class, ex.getCause());
    }
}