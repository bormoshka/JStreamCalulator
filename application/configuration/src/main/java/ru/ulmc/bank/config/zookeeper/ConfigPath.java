package ru.ulmc.bank.config.zookeeper;

import lombok.Getter;
import ru.ulmc.bank.entities.configuration.Currency;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

@Getter
public enum ConfigPath {
    CURRENCY("lists/currency", Currency.class),
    SYMBOL("lists/symbol", SymbolConfig.class),
    CONFIG("app/commonConfig", String.class);


    private final String path;
    private final Class entityClass;

    ConfigPath(String path, Class entityClass) {
        this.path = path;
        this.entityClass = entityClass;
    }
}
