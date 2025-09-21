package com.DigitalFixedDepositSystem.controller;

import com.DigitalFixedDepositSystem.dto.AuthRequest;
import com.DigitalFixedDepositSystem.dto.AuthResponse;
import com.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.DigitalFixedDepositSystem.dto.UserProfileResponse;
import com.DigitalFixedDepositSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public CompletionStage<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request)
                .thenApply(v -> ResponseEntity.ok("User registered successfully"));
    }

    @PostMapping("/login")
    public CompletionStage<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/me")
    public CompletionStage<ResponseEntity<UserProfileResponse>> getCurrentUser() {
        return authService.getCurrentUser()
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/admin/register")
    public CompletionStage<ResponseEntity<String>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return authService.registerAdmin(request)
                .thenApply(v -> ResponseEntity.ok("User registered successfully"));
    }
}