package ru.ulmc.bank;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.junit.Test;
import ru.ulmc.bank.compute.IgniteQuoteService;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.QuoteKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class IgniteTest implements Serializable {
    public static final String QUOTE = "quote";
    private static final double spread = 0.2;
    private final List<BaseQuote> baseQuotes = new ArrayList<>();
    private LocalDateTime date = LocalDateTime.now();

    public static BaseQuote createBaseQuote(String symbol, double value, LocalDateTime date) {
        Set<BasePrice> prices = new HashSet<>();

        prices.add(new BasePrice(0, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(100, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(300, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));
        prices.add(new BasePrice(500, BigDecimal.valueOf(value - spread), BigDecimal.valueOf(value + spread)));

        return new BaseQuote(UUID.randomUUID().toString(), date, symbol, prices);
    }

    private static void visitUsingAffinityRun() {
        Ignite ignite = Ignition.ignite();

        final IgniteCache<QuoteKey, BasePrice> cache = ignite.cache(QUOTE);


        ignite.compute().affinityRun(QUOTE, "USD/RUB",
                () -> {
                   // System.out.println("Co-located using affinityRun [value=" + cache. + ']');
                });

    }

    @Test
    public void test() {
        new Random().doubles(40, 55).limit(10000)
                .forEach(this::createFakeBaseQuote);

        try (IgniteQuoteService ignite = IgniteQuoteService.open("192.168.2.8")) {

            // Execute collection of Callables on the grid.
            IgniteCache<QuoteKey, BaseQuote> cache = ignite.ignite().cache(QUOTE);
            baseQuotes.forEach(bq -> {
                cache.put(new QuoteKey(bq.getSymbol(), bq.getId(), bq.getDatetime()), bq);
            });
            visitUsingAffinityRun();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFakeBaseQuote(double value) {
        date = date.minus(10, ChronoUnit.SECONDS);
        baseQuotes.add(createBaseQuote("USD/RUB", value, date));
    }
}
