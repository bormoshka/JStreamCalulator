package ru.ulmc.bank.calculator.service.calculators;

import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.CalculatorResult;

public interface Calculator {
    /**
     * Вычисляет новую котировку
     * @param newQuote
     * @return
     */
    CalculatorResult calc(BaseQuote newQuote);
}
