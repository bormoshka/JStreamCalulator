package ru.ulmc.bank.config.zookeeper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.config.zookeeper.entities.Currency;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;

@Service
public class ConfigurationService {
    @Value("${configuration.url}")
    private String zooUrl;
    private SymbolConfigStorage symbolConfigStorage;

    @PostConstruct
    private void postConstruct() throws Exception {
        symbolConfigStorage = new SymbolConfigStorage(zooUrl);
    }

    public Collection<Currency> getCurrencies() {
        return symbolConfigStorage.getCurrencies();
    }

    public void deleteSymbol(String symbol) {
        symbolConfigStorage.removeSymbolConfig(symbol);
    }

    public Collection<SymbolConfig> getSymbols() {
        return symbolConfigStorage.getSymbolConfigs();
    }

    public void saveSymbols(Collection<SymbolConfig> symbolConfigs) {
        symbolConfigStorage.saveSymbolConfigs(symbolConfigs);
    }

    public void changeActivation(String symbol, boolean isActive) {
        SymbolConfig symbolConfig = symbolConfigStorage.getSymbolConfig(symbol);
        symbolConfig.setActive(isActive);
        symbolConfigStorage.saveSymbolConfigs(Collections.singleton(symbolConfig));
    }
}
