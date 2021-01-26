package com.applin.selmer.util;

public class ShouldNeverHappenedException extends RuntimeException {

    public ShouldNeverHappenedException() {
    }

    public ShouldNeverHappenedException(String message) {
        super(message);
    }

    public ShouldNeverHappenedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShouldNeverHappenedException(Throwable cause) {
        super(cause);
    }

    public ShouldNeverHappenedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
