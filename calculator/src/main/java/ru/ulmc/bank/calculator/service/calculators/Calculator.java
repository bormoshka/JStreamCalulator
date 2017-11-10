package ru.ulmc.bank.calculator.service.calculators;

import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.SymbolConfig;

public interface Calculator {
    /**
     * Вычисляет новую котировку
     * @param newQuote
     * @return
     */
    double calc(SymbolConfig symbolConfig, BaseQuote newQuote);
}
