package com.zeta.DigitalFixedDepositSystem.service;

import com.zeta.DigitalFixedDepositSystem.model.User;
import java.util.UUID;

public interface UserService {
    User findUserById(UUID userId);
}