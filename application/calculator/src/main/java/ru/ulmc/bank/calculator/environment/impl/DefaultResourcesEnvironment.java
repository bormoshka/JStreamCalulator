package ru.ulmc.bank.calculator.environment.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculator.exception.ConfigurationException;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.config.zookeeper.storage.AppConfigStorage;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.core.dao.JpaQuotesDao;
import ru.ulmc.bank.dao.QuotesDao;

@Slf4j
public class DefaultResourcesEnvironment implements ResourcesEnvironment {
    @Getter
    private QuotesDao quotesDao;
    @Getter
    private SymbolConfigStorage symbolConfigStorage;
    @Getter
    private AppConfigStorage appConfigStorage;

    public DefaultResourcesEnvironment(String zooConnectString) {
        try {
            this.symbolConfigStorage = new SymbolConfigStorage(zooConnectString, true);
            this.appConfigStorage = new AppConfigStorage(zooConnectString);
            this.quotesDao = new JpaQuotesDao(appConfigStorage.getProperties());
        } catch (Exception e) {
            log.error("Failed to initialize environment", e);
            throw new ConfigurationException(e);
        }
    }

}
