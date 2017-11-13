package ru.ulmc.bank.dao.impl;

import org.springframework.stereotype.Component;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Price;
import ru.ulmc.bank.entities.persistent.financial.Quote;

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

    public FakeQuotesDao(String symbol, List<BaseQuote> baseQuotes) {
        baseQuotesBySymbol.put(symbol, baseQuotes);
    }

    public static BaseQuote createBaseQuote(String symbol, double value, LocalDateTime date) {
        Set<BasePrice> prices = new HashSet<>();

        prices.add(new BasePrice(000, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(100, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(300, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(500, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));

        return new BaseQuote(UUID.randomUUID().toString(), date, symbol, prices);
    }

    @PostConstruct
    private void postConstruct() {
        baseQuotesBySymbol.put(symbol, new ArrayList<>());
        new Random().doubles(40, 55).limit(10000)
                .forEach(this::createFakeBaseQuote);
    }

    private void createFakeBaseQuote(double value) {
        date = date.minus(secondsToMinus, ChronoUnit.SECONDS);
        baseQuotesBySymbol.get(symbol).add(createBaseQuote(symbol, value, date));
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
    public ArrayList<AverageQuote> getDailyAverageBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<BaseQuote> lastQuotes = getLastBaseQuotes(symbol, startDateTime, endDateTime);
        ArrayList<AverageQuote> avgQuotes = new ArrayList<>();
        Map<LocalDate, List<BaseQuote>> groupBaseQuotes = lastQuotes.stream()
                .collect(Collectors.groupingBy(o -> o.getDatetime().toLocalDate()));

        for (Map.Entry<LocalDate, List<BaseQuote>> pair : groupBaseQuotes.entrySet()) {
            avgQuotes.add(new AverageQuote(pair.getKey().atStartOfDay(), symbol,
                    getAvgBid(pair.getValue()), getAvgOffer(pair.getValue())));
        }
        avgQuotes.sort((o1, o2) -> {
            if (o1.getDatetime().isAfter(o2.getDatetime())) {
                return 1;
            } else if (o1.getDatetime().isBefore(o2.getDatetime())) {
                return -1;
            }
            return 0;
        });
        return avgQuotes;
    }

    private boolean isDateInRange(LocalDateTime check, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        boolean fits = endDateTime == null || check.isBefore(endDateTime);
        return fits && (startDateTime == null || check.isAfter(startDateTime));
    }

    private Double getAvgBid(List<BaseQuote> quotes) {
        Set<Price> prices = getPricesForZeroVolume(quotes);
        double result = 0;
        for (Price p : prices) {
            result += p.getBid().doubleValue();
        }
        return result / prices.size();
    }

    private Double getAvgOffer(List<BaseQuote> quotes) {
        Set<Price> prices = getPricesForZeroVolume(quotes);
        double result = 0;
        for (Price p : prices) {
            result += p.getOffer().doubleValue();
        }
        return result / prices.size();
    }

    private Set<Price> getPricesForZeroVolume(List<BaseQuote> quotes) {
        Set<Price> prices = new HashSet<>();
        for (BaseQuote quote : quotes) {
            for (Price p : quote.getPrices()) {
                if (p.getVolume() == 0) {
                    prices.add(p);
                }
            }
        }
        return prices;
    }
}
