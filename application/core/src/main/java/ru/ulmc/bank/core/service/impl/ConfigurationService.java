package ru.ulmc.bank.core.service.impl;

import org.springframework.stereotype.Service;
import ru.ulmc.bank.entities.configuration.Currency;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.util.Collection;
import java.util.Collections;
@Service
public class ConfigurationService {
    public Collection<Currency> getCurrencies(){
        return Collections.EMPTY_LIST;
    }
    public void deleteSymbol(String symbol){

    }
    public Collection<SymbolConfig> getSymbols(){
        return Collections.EMPTY_LIST;
    }
}
