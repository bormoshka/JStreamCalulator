package ru.ulmc.generator.logic.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class Scenario implements Serializable {
    private String uuid;
    private String name;
    private List<ScenarioStep> steps = new ArrayList<>();

    public Scenario(String name) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
    }

    public ScenarioStep addStep(Trend trend, int duration, double step, double volatility, TimeUnit timeUnits) {
        ScenarioStep e = new ScenarioStep(trend, step, duration, volatility, timeUnits);
        steps.add(e);
        return e;
    }
}
