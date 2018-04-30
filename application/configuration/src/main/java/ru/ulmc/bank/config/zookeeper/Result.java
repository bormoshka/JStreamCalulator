package ru.ulmc.bank.config.zookeeper;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Result<T> {
    private String nodeId;
    private T data;

    private String errorMessage;

    private Throwable exception;

    Result(T data) {
        this.data = data;
    }

    Result(String node, T data) {
        this.nodeId = node;
        this.data = data;
    }

    Result(String errorMessage) {
        this(errorMessage, (Throwable) null);
    }

    Result(String errorMessage, Throwable exception) {
        this.errorMessage = errorMessage;
        this.exception = exception;
    }
}
