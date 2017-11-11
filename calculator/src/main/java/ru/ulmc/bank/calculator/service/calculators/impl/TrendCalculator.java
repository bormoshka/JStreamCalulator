package ru.ulmc.bank.calculator.service.calculators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.dao.SymbolDao;
import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.SymbolConfig;
import ru.ulmc.bank.calculator.service.calculators.Calculator;

@Component
public class TrendCalculator implements Calculator {
    private final QuotesDao quotesDao;
    private final SymbolDao symbolDao;
    private int timeSeries = 90;

    @Autowired
    public TrendCalculator(QuotesDao dao, SymbolDao symbolDao) {
        this.quotesDao = dao;
        this.symbolDao = symbolDao;
    }

    //расчет прогнозной котировки методом наименьших квадратов
    @Override
    public double calc(SymbolConfig symbolConfig, BaseQuote newQuote) {







        return 0;
    }
}
