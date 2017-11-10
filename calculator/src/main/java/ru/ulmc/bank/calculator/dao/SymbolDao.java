package ru.ulmc.bank.calculator.dao;

import ru.ulmc.bank.calculator.entity.SymbolConfig;

/**
 * Хранилище параметров валютных пар
 */
public interface SymbolDao {
    SymbolConfig getSymbol(String symbol);
}
