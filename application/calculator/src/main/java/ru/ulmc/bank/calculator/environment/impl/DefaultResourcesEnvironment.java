package ru.ulmc.bank.calculator.environment.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculator.exception.ConfigurationException;
import ru.ulmc.bank.calculator.service.impl.DefaultCalcService;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;

@Slf4j
public class DefaultResourcesEnvironment implements ResourcesEnvironment {
    @Getter
    private QuotesDao quotesDao;
    @Getter
    private SymbolConfigStorage symbolConfigStorage;

    public DefaultResourcesEnvironment(String zooConnectString) {
        this.quotesDao = new FakeQuotesDao();
        try {
            this.symbolConfigStorage = new SymbolConfigStorage(zooConnectString);
        } catch (Exception e) {
            log.error("Failed to initialize environment", e);
            throw new ConfigurationException(e);
        }
    }

}
