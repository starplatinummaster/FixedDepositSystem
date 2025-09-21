package com.DigitalFixedDepositSystem.service.impl;

import com.DigitalFixedDepositSystem.dto.AuthRequest;
import com.DigitalFixedDepositSystem.dto.AuthResponse;
import com.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.DigitalFixedDepositSystem.exception.InvalidCredentialsException;
import com.DigitalFixedDepositSystem.exception.UserAlreadyExistsException;
import com.DigitalFixedDepositSystem.model.User;
import com.DigitalFixedDepositSystem.repository.UserRepository;
import com.DigitalFixedDepositSystem.security.JWTUtil;
import com.DigitalFixedDepositSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static com.DigitalFixedDepositSystem.utils.AppConstants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final Executor asyncExecutor;

    @Override
    public CompletionStage<Void> register(RegisterRequest request) {
        return CompletableFuture.runAsync(() -> performRegistration(request), asyncExecutor);
    }

    @Override
    public CompletionStage<Void> registerAdmin(RegisterRequest request) {
        return CompletableFuture.runAsync(() -> performAdminRegistration(request), asyncExecutor);
    }

    @Transactional
    protected void performRegistration(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(String.format(EMAIL_ALREADY_EXISTS, request.getEmail()));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        User u = userRepository.save(user);
        System.out.println(u.getRole());
    }

    @Transactional
    protected void performAdminRegistration(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(String.format(EMAIL_ALREADY_EXISTS, request.getEmail()));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                .build();
        User u = userRepository.save(user);
        System.out.println(u.getRole());
    }

    @Override
    public CompletionStage<AuthResponse> login(AuthRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                String token = jwtUtil.generateToken(userDetails);
                return new AuthResponse(token);
            } catch (AuthenticationException e) {
                throw new InvalidCredentialsException(INVALID_EMAIL_OR_PASSWORD);
            }
        }, asyncExecutor);
    }

    @Override
    public CompletionStage<UserProfileResponse> getCurrentUser() {
        return CompletableFuture.supplyAsync(() -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new InvalidCredentialsException(USER_NOT_AUTHENTICATED);
            }
            return fetchUserProfile(auth.getName());
        }, asyncExecutor);
    }

    @Transactional(readOnly = true)
    protected UserProfileResponse fetchUserProfile(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserProfileResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .orElseThrow(() -> new InvalidCredentialsException(String.format(USER_NOT_FOUND, email)));
    }
}