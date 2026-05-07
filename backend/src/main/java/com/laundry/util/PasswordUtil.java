package com.laundry.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password Utility using BCrypt
 */
public class PasswordUtil {

    private static final int LOG_ROUNDS = 10;

    /**
     * Hash a password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /**
     * Verify a password against a hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
