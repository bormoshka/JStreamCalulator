package ru.ulmc.bank.core.common.exception;

import lombok.Getter;

public class AuthenticationException extends Exception {
    @Getter
    private boolean isSystemFault = false;


    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
        isSystemFault = true;
    }

}
