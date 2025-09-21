package com.DigitalFixedDepositSystem.service.impl;

import com.DigitalFixedDepositSystem.exception.UserNotFoundException;
import com.DigitalFixedDepositSystem.model.User;
import com.DigitalFixedDepositSystem.repository.UserRepository;
import com.DigitalFixedDepositSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.DigitalFixedDepositSystem.utils.AppConstants.ErrorMessage.USER_NOT_FOUND;

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