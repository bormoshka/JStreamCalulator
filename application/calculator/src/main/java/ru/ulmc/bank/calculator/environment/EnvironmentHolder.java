package ru.ulmc.bank.calculator.environment;

import ru.ulmc.bank.calculator.environment.impl.DefaultResourcesEnvironment;
import ru.ulmc.bank.calculator.environment.impl.DefaultServicesEnvironment;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;

import java.util.Map;

public class EnvironmentHolder {
    private static ResourcesEnvironment RESOURCES = null;
    private static ServiceEnvironment SERVICES = null;

    public static synchronized void initResources(String zookeeperString) {
        RESOURCES = new DefaultResourcesEnvironment(zookeeperString);
    }

    public static synchronized void initServices(Map<String, Calculator> calculatorMap) {
        SERVICES = new DefaultServicesEnvironment(calculatorMap);
    }

    public static ResourcesEnvironment getResources() {
        return RESOURCES;
    }

    public static ServiceEnvironment getServices() {
        return SERVICES;
    }
}
