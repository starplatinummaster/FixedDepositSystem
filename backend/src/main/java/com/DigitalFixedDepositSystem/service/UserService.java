package com.DigitalFixedDepositSystem.service;

import com.DigitalFixedDepositSystem.model.User;
import java.util.UUID;

public interface UserService {
    User findUserById(UUID userId);
}