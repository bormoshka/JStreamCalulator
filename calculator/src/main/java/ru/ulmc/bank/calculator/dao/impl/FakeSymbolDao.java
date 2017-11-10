package ru.ulmc.bank.calculator.dao.impl;

import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.SymbolDao;
import ru.ulmc.bank.calculator.entity.SymbolConfig;
import ru.ulmc.bank.calculator.exception.ConfigurationException;

@Component
public class FakeSymbolDao implements SymbolDao {
    private SymbolConfig rubUsd = new SymbolConfig("RUB/USD");

    @Override
    public SymbolConfig getSymbol(String symbol) {
        if (rubUsd.getSymbol().equals(symbol)) {
            return rubUsd;
        }
        throw new ConfigurationException("Symbol with name " + symbol + " was not found!");
    }
}
