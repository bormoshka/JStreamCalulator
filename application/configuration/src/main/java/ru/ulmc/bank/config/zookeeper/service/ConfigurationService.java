package ru.ulmc.bank.config.zookeeper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.entities.configuration.Currency;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import javax.annotation.PostConstruct;
import java.util.Collection;

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
}
