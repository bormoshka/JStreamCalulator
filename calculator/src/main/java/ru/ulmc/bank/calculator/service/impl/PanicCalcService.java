package ru.ulmc.bank.calculator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.Quote;
import ru.ulmc.bank.calculator.entity.SymbolConfig;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculator.service.calculators.impl.MnkTrendCalculator;

/**
 * Сервис, отвечающий за вычисление котировок при критических отклонениях.
 */
@Component
public class PanicCalcService implements CalcService {
    private final DynamicCalculator dynamicCalculator;
    private final MnkTrendCalculator mnkTrendCalculator;

    @Autowired
    public PanicCalcService(DynamicCalculator dynamicCalculator, MnkTrendCalculator mnkTrendCalculator) {
        this.dynamicCalculator = dynamicCalculator;
        this.mnkTrendCalculator = mnkTrendCalculator;
    }

    @Override
    public Quote calculateQuoteForSymbol(SymbolConfig symbolConfig, BaseQuote newQuote) {
        //todo: Реализовать вычисление аварийного расширения спреда
        return null;
    }
}
