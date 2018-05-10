package ru.ulmc.generator.logic;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.ulmc.generator.logic.beans.QuoteEntity;
import ru.ulmc.generator.logic.beans.StreamTask;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Controller
@Slf4j
public class StreamController {
    private final PublishingController publisher;
    private final Map<String, ScheduledFuture> futures = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture> scheduledForReschedule = new ConcurrentHashMap<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    @Setter
    private Consumer<String> messageConsumer;

    private boolean isStreamMuted = false;

    @Autowired
    public StreamController(PublishingController publisher) {
        this.publisher = publisher;
    }

    @PreDestroy
    public void closeAll() {
        executor.shutdown();
    }

    public void setStreamMuted(boolean streamMuted) {
        log.info("Changing stream mute status to {}", streamMuted ? "MUTED" : "NOT MUTED");
        isStreamMuted = streamMuted;
    }

    public void startNewTask(String symbol, double bid, double offer, double volatility, double interval) {
        if (futures.keySet().contains(symbol)) {
            log.info("Stream already enabled for {}", symbol);
            return;
        }
        schedule(new StreamTask(symbol, bid, offer, volatility, interval, null));
    }

    public void startNewTask(ScenarioProcess process) {
        if (futures.keySet().contains(process.getSymbol())) {
            log.info("Stream already enabled for {}", process.getSymbol());
            return;
        }
        schedule(process);
    }

    private void schedule(StreamTask task) {
        log.info("Init streaming for {}", task.getSymbol());
        futures.put(task.getSymbol(), executor.scheduleAtFixedRate(() -> {
            if (isStreamMuted) {
                return;
            }
            QuoteEntity quote = task.createQuote();
            log.debug("Streaming with interval: {} for: {} quote id: {}", task.getInterval(), quote.getSymbol(), quote.getId());
            publisher.publish(quote);
            messageConsumer.accept("[STREAM] " + task.getSymbol() + " QUID: " + quote.getId());
        }, 500, (long) (task.getInterval() * 1000), TimeUnit.MILLISECONDS));
    }

    public void stopStreaming() {
        log.info("Stopping ALL stream ");
        futures.keySet().stream().map(futures::remove).forEach(sf -> sf.cancel(true));
    }

    public void stopStreaming(String symbol) {
        ScheduledFuture scheduledFuture = futures.remove(symbol);
        if (scheduledFuture != null) {
            log.info("Stopping stream for {}", symbol);
            scheduledFuture.cancel(true);
        } else {
            log.warn("Can't find ScheduledFuture for {}", symbol);
        }
    }

    public void reschedule(StreamTask task) {
        scheduledForReschedule.compute(task.getSymbol(), (s, scheduledFuture) -> {
            if (scheduledFuture != null && !(scheduledFuture.isCancelled() || scheduledFuture.isDone())) {
                scheduledFuture.cancel(true);
            }
            return executor.schedule(() -> {
                log.info("Rescheduling: {}", task);
                stopStreaming(task.getSymbol());
                schedule(task);
            }, 1500, TimeUnit.MILLISECONDS);
        });
    }

    private void schedule(ScenarioProcess process) {
        log.info("Init streaming ScenarioProcess for {}", process.getSymbol());
        List<QuoteEntity> entities = process.getQuotesToPublish();
        futures.put(process.getSymbol(),
                executor.scheduleAtFixedRate(() -> {
            if (isStreamMuted) {
                return;
            }
            if (entities.isEmpty()) {
                reschedule(process);
                return;
            }
            QuoteEntity remove = entities.remove(0);
            log.debug("Scenario with i: {} for: {} Quote: {}", process.getInterval(), process.getSymbol(), remove);
            publisher.publish(remove);
            messageConsumer.accept("[SCENARIO STREAM] " + remove.getSymbol() + " QUID: " + remove.getId());
        }, 1000, (long) (process.getInterval()*1000), TimeUnit.MILLISECONDS));
    }

    public void reschedule(ScenarioProcess process) {
        scheduledForReschedule.compute(process.getSymbol(), (s, scheduledFuture) -> {
            if (scheduledFuture != null && !(scheduledFuture.isCancelled() || scheduledFuture.isDone())) {
                scheduledFuture.cancel(true);
            }
            return executor.schedule(() -> {
                log.info("Rescheduling scenario for: {}", process);
                stopStreaming(process.getSymbol());
                schedule(process);
            }, 1500, TimeUnit.MILLISECONDS);
        });
    }
}