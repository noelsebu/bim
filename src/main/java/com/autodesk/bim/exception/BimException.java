package com.autodesk.bim.exception;

public class BimException extends RuntimeException {

    public BimException(String message) {
        super(message);
    }

    public BimException(String message, Throwable cause) {
        super(message, cause);
    }
}
