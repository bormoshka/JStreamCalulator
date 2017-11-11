package ru.ulmc.bank.calculator.dao.impl;

import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.Price;
import ru.ulmc.bank.calculator.entity.Quote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class FakeQuotesDao implements QuotesDao {
    private static final double spread = 0.2;
    private static final int secondsToMinus = 5;
    private LocalDateTime date = LocalDateTime.now();
    private final String symbol = "RUB/USD";
    private final HashMap<String, BaseQuote> baseQuotesBySymbol = new HashMap<>();
    private final HashMap<String, Quote> calcQuotesBySymbol = new HashMap<>();

    public FakeQuotesDao() {
        new Random().doubles(10, 2).limit(10000)
                .forEach(value -> baseQuotesBySymbol.put(symbol, createBaseQuote(value)));
    }

    private BaseQuote createBaseQuote(double value) {
        Set<Price> prices = new HashSet<>();

        prices.add(new Price(0, BigDecimal.valueOf(value), BigDecimal.valueOf(value - spread)));
        prices.add(new Price(100, BigDecimal.valueOf(value), BigDecimal.valueOf(value - spread)));
        prices.add(new Price(300, BigDecimal.valueOf(value), BigDecimal.valueOf(value - spread)));
        prices.add(new Price(500, BigDecimal.valueOf(value), BigDecimal.valueOf(value - spread)));
        date = date.minus(secondsToMinus, ChronoUnit.SECONDS);
        return new BaseQuote(UUID.randomUUID().toString(), date, symbol, prices);
    }

    @Override
    public BaseQuote getLastBaseQuote(String symbol) {
        return null;
    }

    @Override
    public BaseQuote getLastBaseQuotes(String symbol, int count) {
        return null;
    }

    @Override
    public BaseQuote getLastBaseQuotes(String symbol, LocalDateTime startDateTime) {
        return null;
    }

    @Override
    public BaseQuote getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return null;
    }

    @Override
    public Quote getLastCalcQuote(String symbol) {
        return null;
    }

    @Override
    public Quote getLastCalcQuote(String symbol, int count) {
        return null;
    }
}
