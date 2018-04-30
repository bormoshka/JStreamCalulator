package ru.ulmc.bank.calculator.service;

import ru.ulmc.bank.calculator.service.transfer.CalculationOutput;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

public interface CalcService {

    /**
     * Вычисляет пригодные для публикации котировки
     *
     * @param symbolConfig Конфигурация валютной пары
     * @return Вычисленная котировка
     */
    Quote calculateQuoteForSymbol(SymbolConfig symbolConfig, CalculationOutput quotePreResult);
}
