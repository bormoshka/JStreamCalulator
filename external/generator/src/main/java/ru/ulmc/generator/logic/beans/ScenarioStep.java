package ru.ulmc.generator.logic.beans;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of="uuid")
public class ScenarioStep implements Serializable {
    private String uuid;
    private Trend trend;
    private double delta;
    private int duration;
    private double volatility;
    private TimeUnit timeUnits;

    public ScenarioStep(Trend trend, double step, int duration, double volatility, TimeUnit timeUnits) {
        this.uuid = UUID.randomUUID().toString();
        this.trend = trend;
        this.delta = step;
        this.duration = duration;
        this.volatility = volatility;
        this.timeUnits = timeUnits;
    }
}
