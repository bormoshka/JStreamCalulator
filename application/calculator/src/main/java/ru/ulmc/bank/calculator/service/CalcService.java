package ru.ulmc.bank.calculator.service;

import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

public interface CalcService {

    /**
     * Вычисляет пригодные для публикации котировки
     *
     * @param symbolConfig Конфигурация валютной пары
     * @param newQuote Новая котировка
     * @return Вычисленная котировка
     */
    Quote calculateQuoteForSymbol(SymbolConfig symbolConfig, BaseQuote newQuote);
}