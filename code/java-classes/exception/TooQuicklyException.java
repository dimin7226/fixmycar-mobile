package com.fixmycar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_EARLY)
public class TooQuicklyException extends RuntimeException {
    public TooQuicklyException(String message) {
        super(message);
    }
}