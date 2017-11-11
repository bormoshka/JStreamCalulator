package ru.ulmc.bank.calculator.dao.impl;

import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.AverageQuote;
import ru.ulmc.bank.calculator.entity.Price;
import ru.ulmc.bank.calculator.entity.Quote;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FakeQuotesDao implements QuotesDao {
    private static final double spread = 0.2;
    private static final int secondsToMinus = 5;
    private final String symbol = "RUB/USD";
    private final HashMap<String, List<BaseQuote>> baseQuotesBySymbol = new HashMap<>();
    private final HashMap<String, Quote> calcQuotesBySymbol = new HashMap<>();
    private LocalDateTime date = LocalDateTime.now();

    public FakeQuotesDao() {

    }

    @PostConstruct
    private void postConstruct() {
        baseQuotesBySymbol.put(symbol, new ArrayList<>());
        new Random().doubles(40, 55).limit(10000)
                .forEach(this::createFakeBaseQuote);
    }

    private void createFakeBaseQuote(double value) {
        date = date.minus(secondsToMinus, ChronoUnit.SECONDS);
        baseQuotesBySymbol.get(symbol).add(createBaseQuote(value, date));
    }

    private BaseQuote createBaseQuote(double value, LocalDateTime date) {
        Set<Price> prices = new HashSet<>();

        prices.add(new Price(000, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new Price(100, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new Price(300, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new Price(500, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));

        return new BaseQuote(UUID.randomUUID().toString(), date, symbol, prices);
    }

    @Override
    public BaseQuote getLastBaseQuote(String symbol) {
        return null;
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, int count) {
        return null;
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime) {
        return null;
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<BaseQuote> baseQuotesThisSymbol = baseQuotesBySymbol.get(symbol);
        List<BaseQuote> baseQuotesThisSymbolForDatePeriod = new ArrayList<>();
        for (BaseQuote quote : baseQuotesThisSymbol) {
            if (isDateInRange(quote.getDatetime(), startDateTime, endDateTime)) {
                baseQuotesThisSymbolForDatePeriod.add(quote);
            }
        }
        return baseQuotesThisSymbolForDatePeriod;
    }

    @Override
    public Quote getLastCalcQuote(String symbol) {
        return null;
    }

    @Override
    public Quote getLastCalcQuote(String symbol, int count) {
        return null;
    }

    @Override
    public List<AverageQuote> getDailyAverageBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<BaseQuote> baseQuotesThisSymbolForDatePeriod = getLastBaseQuotes(symbol, startDateTime, endDateTime);
        List<AverageQuote> baseAverageQuotesThisSymbolForDatePeriod = new ArrayList<>();
        Map<LocalDate, List<BaseQuote>> groupBaseQuotes = baseQuotesThisSymbolForDatePeriod.stream()
                .collect(Collectors.groupingBy(o -> o.getDatetime().toLocalDate()));

        for (Map.Entry<LocalDate, List<BaseQuote>> pair : groupBaseQuotes.entrySet()) {
            baseAverageQuotesThisSymbolForDatePeriod.add(
                    new AverageQuote(pair.getKey().atStartOfDay(), symbol, null));
            //сделать нормальное добавление объекта со средней котировкой в мапу
        }
        return baseAverageQuotesThisSymbolForDatePeriod;
    }

    private boolean isDateInRange(LocalDateTime check, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean fits = endDateTime == null || check.isBefore(endDateTime);
        return fits && (startDateTime == null || check.isAfter(startDateTime));
    }

    private BigDecimal getAvg(List<BaseQuote> quotes){
        for(BaseQuote quote : quotes){
            //написать вычисление среднего
        }
        return null;
    }
}
