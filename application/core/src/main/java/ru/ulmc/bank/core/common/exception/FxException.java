package ru.ulmc.bank.core.common.exception;

public class FxException extends RuntimeException {
    public FxException() {
        super();
    }

    public FxException(String message) {
        super(message);
    }

    public FxException(String message, Throwable cause) {
        super(message, cause);
    }
}
