package ru.ulmc.bank.calculator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculator.service.calculators.impl.MnkTrendCalculator;

/**
 * Сервис, отвечающий за вычисление котировок при нормальных отклонениях.
 */
@Component
public class DefaultCalcService implements CalcService {
    private final DynamicCalculator dynamicCalculator;
    private final MnkTrendCalculator mnkTrendCalculator;

    @Autowired
    public DefaultCalcService(DynamicCalculator dynamicCalculator,
                              MnkTrendCalculator mnkTrendCalculator) {
        this.dynamicCalculator = dynamicCalculator;
        this.mnkTrendCalculator = mnkTrendCalculator;
    }

    @Override
    public Quote calculateQuoteForSymbol(SymbolConfig symbolConfig, BaseQuote newQuote) {
        //todo: Реализовать вычисление по формуле
        return null;
    }
}
