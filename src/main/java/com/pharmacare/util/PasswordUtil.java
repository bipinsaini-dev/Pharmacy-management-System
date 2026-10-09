package com.pharmacare.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() { }

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(10));
    }

    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
