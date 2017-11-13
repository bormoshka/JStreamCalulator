package ru.ulmc.bank.calculator.service.calculators;

import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

public interface Calculator {
    /**
     * Вычисляет новую котировку
     * @param newQuote
     * @return
     */
    CalculatorResult calc(BaseQuote newQuote);
}
