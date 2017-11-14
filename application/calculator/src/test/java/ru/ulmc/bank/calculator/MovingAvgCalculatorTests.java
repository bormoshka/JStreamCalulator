package ru.ulmc.bank.calculator;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ulmc.bank.calculator.service.calculators.impl.MovingAverageTrendCalculator;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
public class MovingAvgCalculatorTests {

    @Test
    public void movingAvgTest() {
        Set<BasePrice> prices = new HashSet<>();
        prices.add(new BasePrice(0, BigDecimal.valueOf(1.42), BigDecimal.valueOf(1.52)));
        LocalDateTime now = LocalDateTime.now();
        BaseQuote newQuote = new BaseQuote(UUID.randomUUID().toString(), now, "RUB/USD", prices);

        List<BaseQuote> baseQuotes = new ArrayList<>();
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.83, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 1, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.86, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 6, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 3.19, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 20, 1)));

        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.42, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 1, 30, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.6, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 2, 50, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.76, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 4, 10, 1)));

        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.76, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 7, 40, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.92, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 8, 8, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.17, LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 10, 0, 1)));


        QuotesDao dao = new FakeQuotesDao("RUB/USD", baseQuotes);
        MovingAverageTrendCalculator calc = new MovingAverageTrendCalculator(dao);
        CalculatorResult result = calc.calc(newQuote);

        Assert.assertEquals(1.86875, result.getResultForBid(), 0.0001);
        Assert.assertEquals(18.702, result.getInaccuracyForBid(), 0.001);
    }
}
