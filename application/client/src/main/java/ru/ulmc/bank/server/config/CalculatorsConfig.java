package ru.ulmc.bank.server.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculators.util.CalculatorInfo;
import ru.ulmc.bank.calculators.util.CalculatorsLocator;

import java.util.Set;

/**
 * Класс, отвечающий за инициализацию пользовательский холей и разрешений.
 */
@Slf4j
@Component
public class CalculatorsConfig implements ApplicationListener<ContextStartedEvent> {
    @Getter
    private Set<CalculatorInfo> calculators;

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        log.info("On context start event collecting calculators");
        calculators = CalculatorsLocator.collect();
    }
}
