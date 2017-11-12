package ru.ulmc.bank.calculator.service.calculators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.CalculatorResult;
import ru.ulmc.bank.calculator.service.calculators.Calculator;

@Component
public class DynamicCalculator implements Calculator {
    private final QuotesDao quotesDao;


    @Autowired
    public DynamicCalculator(QuotesDao dao) {
        this.quotesDao = dao;
    }

    @Override
    public CalculatorResult calc(BaseQuote newQuote) {
        //todo: Реализовать вычисление динамики
        return null;
    }
}
