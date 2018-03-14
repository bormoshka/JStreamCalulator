package ru.ulmc.bank.calculator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculator.service.calculators.impl.OlsTrendCalculator;

/**
 * Сервис, отвечающий за вычисление котировок при критических отклонениях.
 */
@Component
public class PanicCalcService implements CalcService {
    private final DynamicCalculator dynamicCalculator;
    private final OlsTrendCalculator olsTrendCalculator;

    @Autowired
    public PanicCalcService(DynamicCalculator dynamicCalculator, OlsTrendCalculator olsTrendCalculator) {
        this.dynamicCalculator = dynamicCalculator;
        this.olsTrendCalculator = olsTrendCalculator;
    }

    @Override
    public Quote calculateQuoteForSymbol(SymbolConfig symbolConfig, BaseQuote newQuote) {
        //todo: Реализовать вычисление аварийного расширения спреда
        return null;
    }
}
