package com.fixmycar.exception;

public class ProcessingFileException extends RuntimeException {
    public ProcessingFileException(final String message) {
        super(message);
    }
}