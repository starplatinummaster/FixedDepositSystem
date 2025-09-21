package com.zeta.DigitalFixedDepositSystem.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class AppConstants {

    private AppConstants() { }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SuccessMessage {
        public static final String TEMP = "temp";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorMessage {
        public static final String FIXED_DEPOSIT_NOT_FOUND = "Fixed Deposit not found for ID: ";
        public static final String EMAIL_ALREADY_EXISTS = "Email %s already registered";
        public static final String USER_NOT_AUTHENTICATED = "User not authenticated";
        public static final String USER_NOT_FOUND = "User with email: %s not found";
        public static final String INTEREST_RATE_NOT_FOUND = "No interest rate defined for a tenure of %s days.";
        public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class FixedDepositCalculation {
        public static final int COMPOUNDING_FREQUENCY_QUARTERLY = 4;
        public static final int MONTHS_IN_QUARTER = 3;
        public static final int INTEREST_CALCULATION_SCALE = 10;
        public static final int MONETARY_SCALE = 2;
        public static final int PERCENTAGE_DIVISOR = 100;
    }
}
