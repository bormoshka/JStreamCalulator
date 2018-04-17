package ru.ulmc.bank.core.common.exception;

public class UserInputException extends FxException {

    public UserInputException() {
        super();
    }

    public UserInputException(String message) {
        super(message);
    }

    public UserInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
