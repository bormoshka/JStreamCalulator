package ru.ulmc.bank.calculators.impl;

import lombok.NoArgsConstructor;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

@CalcPlugin(name = "Динамический калькулятор",
description = "Не помню зачем он нужен")
@NoArgsConstructor
public class DynamicCalculator implements Calculator {
    private QuotesDao quotesDao;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
    }

    @Override
    public CalculatorResult calc(BaseQuote newQuote) {
        //todo: Реализовать вычисление динамики
        return null;
    }
}
