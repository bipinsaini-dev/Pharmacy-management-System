package com.pharmacare.util;

/** Thrown for expected, user-correctable problems (validation, insufficient stock, ...). */
public class BusinessException extends Exception {
    public BusinessException(String message) {
        super(message);
    }
}
