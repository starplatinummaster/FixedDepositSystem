package com.zeta.DigitalFixedDepositSystem.service.impl;

import com.zeta.DigitalFixedDepositSystem.exception.UserNotFoundException;
import com.zeta.DigitalFixedDepositSystem.model.User;
import com.zeta.DigitalFixedDepositSystem.repository.UserRepository;
import com.zeta.DigitalFixedDepositSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.zeta.DigitalFixedDepositSystem.utils.AppConstants.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
    }
}