package ru.ulmc.bank.config.zookeeper;

public interface Listener<T> {

    default void added(String key, T model) {
    }

    default void updated(String key, T model) {
    }

    default void removed(String key) {
    }
}
