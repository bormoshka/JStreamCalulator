package ru.ulmc.bank.config.zookeeper;

public class Result<T> {
    private T data;

    private String errorMessage;

    private Throwable exception;

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Throwable getException() {
        return exception;
    }

    Result(T data) {
        this.data = data;
    }

    Result(String errorMessage) {
        this(errorMessage, null);
    }

    Result(String errorMessage, Throwable exception) {
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Result {" +
                " Data: " + data +
                " ErrorMessage: " + errorMessage +
                " Exception: " + exception +
                " }";
    }
}
