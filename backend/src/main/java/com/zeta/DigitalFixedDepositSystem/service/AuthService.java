package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.dto.AuthRequest;
import com.zeta.DigitalFixedDepositSystem.dto.AuthResponse;
import com.zeta.DigitalFixedDepositSystem.dto.RegisterRequest;
import com.zeta.DigitalFixedDepositSystem.dto.UserProfileResponse;

import java.util.concurrent.CompletionStage;

public interface AuthService {
    CompletionStage<Void> register(RegisterRequest request);
    CompletionStage<Void> registerAdmin(RegisterRequest request);
    CompletionStage<AuthResponse> login(AuthRequest request);
    CompletionStage<UserProfileResponse> getCurrentUser();
}