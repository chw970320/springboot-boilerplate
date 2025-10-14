package com.boilerplate.core.exception;

/**
 * 비즈니스 로직에서 예외가 발생할 때 발생하는 예외.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
