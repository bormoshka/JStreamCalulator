package ru.ulmc.bank.calculator.environment.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculator.environment.ServiceEnvironment;
import ru.ulmc.bank.calculator.service.impl.DefaultCalcService;
import ru.ulmc.bank.calculators.Calculator;

import java.util.Map;

@Slf4j
public class DefaultServicesEnvironment implements ServiceEnvironment {
    @Getter
    private DefaultCalcService defaultCalcService;

    public DefaultServicesEnvironment(Map<String, Calculator> calculatorMap) {
        defaultCalcService = new DefaultCalcService();
    }
}
