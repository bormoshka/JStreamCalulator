package ru.ulmc.bank.calculator;

import org.junit.Assert;
import org.junit.Test;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.impl.MovingAverageTrendCalculator;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;
import ru.ulmc.bank.config.zookeeper.storage.AppConfigStorage;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.math.BigDecimal.valueOf;

public class MovingAvgCalculatorTests {

    @Test
    public void movingAvgTest() {
        SymbolConfig sc = new SymbolConfig("RUB/USD", 0, 0, 100, 100);
        Set<BasePrice> prices = new HashSet<>();
        prices.add(new BasePrice(0, valueOf(1.42), valueOf(1.52)));
        LocalDateTime now = LocalDateTime.now();
        BaseQuote newQuote = new BaseQuote(UUID.randomUUID().toString(), now, "RUB/USD", prices);

        long sec = 9;
        List<BaseQuote> baseQuotes = new ArrayList<>();
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.83,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.86,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 3.19,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.42,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.6,   LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.76,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.76,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.92,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.17,  LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));


        QuotesDao dao = new FakeQuotesDao("RUB/USD", baseQuotes);
        MovingAverageTrendCalculator calc = new MovingAverageTrendCalculator();
        calc.initialize(new ResourcesEnvironment() {
            @Override
            public QuotesDao getQuotesDao() {
                return dao;
            }

            @Override
            public SymbolConfigStorage getSymbolConfigStorage() {
                return null;
            }

            @Override
            public AppConfigStorage getAppConfigStorage() {
                return null;
            }
        });
        CalculatorResult result = calc.calc(new CalcSourceQuote(newQuote, sc));

        Assert.assertEquals(1.36, result.getResultForBid().doubleValue(), 0.01);
        Assert.assertEquals(2.31, result.getResultForOffer().doubleValue(), 0.01);
    }
}
