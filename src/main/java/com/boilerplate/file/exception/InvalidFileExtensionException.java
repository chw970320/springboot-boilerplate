package com.boilerplate.file.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileExtensionException extends RuntimeException {

    public InvalidFileExtensionException(String message) {
        super(message);
    }

    public InvalidFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
