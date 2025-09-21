package com.DigitalFixedDepositSystem.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidCredentialsException extends BadCredentialsException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}