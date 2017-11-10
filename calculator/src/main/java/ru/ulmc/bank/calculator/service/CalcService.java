package ru.ulmc.bank.calculator.service;

import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.Quote;
import ru.ulmc.bank.calculator.entity.SymbolConfig;

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
