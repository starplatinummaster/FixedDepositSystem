package com.DigitalFixedDepositSystem.service;

import com.DigitalFixedDepositSystem.dto.AuthRequest;
import com.DigitalFixedDepositSystem.dto.AuthResponse;
import com.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.DigitalFixedDepositSystem.dto.UserProfileResponse;

import java.util.concurrent.CompletionStage;

public interface AuthService {
    CompletionStage<Void> register(RegisterRequest request);
    CompletionStage<Void> registerAdmin(RegisterRequest request);
    CompletionStage<AuthResponse> login(AuthRequest request);
    CompletionStage<UserProfileResponse> getCurrentUser();
}