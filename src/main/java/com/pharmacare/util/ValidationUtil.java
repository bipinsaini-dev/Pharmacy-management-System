package com.pharmacare.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{10,13}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{3,30}$");

    private ValidationUtil() { }

    public static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /** Null-safe trim: never returns null. */
    public static String clean(String s) { return s == null ? "" : s.trim(); }

    public static boolean isEmail(String s) { return s != null && EMAIL.matcher(s.trim()).matches(); }

    public static boolean isPhone(String s) { return s != null && PHONE.matcher(s.trim()).matches(); }

    public static boolean isUsername(String s) { return s != null && USERNAME.matcher(s.trim()).matches(); }

    /** Parses a decimal; returns null when blank or invalid. */
    public static BigDecimal toDecimal(String s) {
        try {
            return isBlank(s) ? null : new BigDecimal(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Parses an int; returns null when blank or invalid. */
    public static Integer toInt(String s) {
        try {
            return isBlank(s) ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Parses yyyy-MM-dd; returns null when blank or invalid. */
    public static Date toDate(String s) {
        try {
            return isBlank(s) ? null : Date.valueOf(s.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Returns null for blank so optional columns are stored as NULL. */
    public static String nullIfBlank(String s) { return isBlank(s) ? null : s.trim(); }
}
