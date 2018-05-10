package ru.ulmc.bank.calculators;

import ru.ulmc.bank.bean.IPrice;
import ru.ulmc.bank.core.common.exception.FxException;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;

public interface Calculator {
    /**
     * Инициализация вычислителя
     * @param environment
     * @return
     */
    Calculator initialize(ResourcesEnvironment environment);

    /**
     * Вычисляет новую котировку
     *
     * @param newQuote
     * @return
     */
    CalculatorResult calc(CalcSourceQuote newQuote);

}
