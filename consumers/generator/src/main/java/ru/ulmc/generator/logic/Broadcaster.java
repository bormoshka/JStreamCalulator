package ru.ulmc.generator.logic;

import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class Broadcaster implements Serializable {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Map<String, List<BroadcastListener>> listeners = new ConcurrentHashMap<>();


    @PreDestroy
    public void closeAll() {
        executorService.shutdown();
    }

    public synchronized void register(Class event, BroadcastListener listener) {
        listeners.computeIfAbsent(event.getCanonicalName(), s -> new LinkedList<>()).add(listener);
    }

    public synchronized void unregister(Class event, BroadcastListener listener) {
        unregister(event.getCanonicalName(), listener);
    }

    public synchronized void unregister(String eventName, BroadcastListener listener) {
        log.trace("unregister {}, {}", eventName, listener);
        listeners.computeIfPresent(eventName, (s, broadcastListeners) -> {
            broadcastListeners.remove(listener);
            return broadcastListeners;
        });
    }

    public synchronized <T> void broadcast(final BroadcastEvent<T> message) {
        handleEvent(message.getContent().getClass().getCanonicalName(), message);
    }

    private <T> void handleEvent(String eventClassName, BroadcastEvent<T> event) {
        listeners.getOrDefault(eventClassName, Collections.emptyList())
                .forEach(listener -> executorService.execute(() -> {
                            try {
                                if (listener != null) {
                                    Platform.runLater(() -> listener.receiveBroadcast(event));

                                }
                            } catch (Exception exception) {
                                log.warn("Exception", exception);
                                unregister(eventClassName, listener);
                            }
                        }
                ));
    }

    public static abstract class BroadcastListener<T> {
        private UUID id = UUID.randomUUID();

        public BroadcastListener() {
        }

        public abstract void receiveBroadcast(BroadcastEvent<T> message);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BroadcastListener<?> that = (BroadcastListener<?>) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BroadcastEvent<T> implements Serializable {
        private T content;
    }

}
