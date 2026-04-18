package com.bms.util;

import java.security.SecureRandom;

public final class AccountNumberGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AccountNumberGenerator() {
    }

    public static String generate() {
        StringBuilder builder = new StringBuilder("41");
        while (builder.length() < 12) {
            builder.append(SECURE_RANDOM.nextInt(10));
        }
        return builder.toString();
    }
}
