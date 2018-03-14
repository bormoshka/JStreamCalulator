package ru.ulmc.bank.calculator.environment;

import ru.ulmc.bank.calculator.environment.impl.DefaultResourcesEnvironment;

public class EnvironmentHolder {
    private static ResourcesEnvironment INSTANCE = null;

    public static synchronized void init(String zookeeperString) {
        INSTANCE = new DefaultResourcesEnvironment(zookeeperString);
    }

    public static ResourcesEnvironment get() {
        return INSTANCE;
    }
}
