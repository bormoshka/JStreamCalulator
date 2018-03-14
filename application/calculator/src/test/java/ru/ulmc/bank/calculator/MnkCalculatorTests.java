package ru.ulmc.bank.calculator;

import org.junit.Assert;
import org.junit.Test;
import ru.ulmc.bank.calculator.service.calculators.impl.OlsTrendCalculator;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class MnkCalculatorTests {

    @Test
    public void mnkTest() {
        Set<BasePrice> prices = new HashSet<>();
        prices.add(new BasePrice(0, 1.42, 1.52));
        BaseQuote newQuote = new BaseQuote(UUID.randomUUID().toString(), LocalDateTime.now(), "RUB/USD", prices);

        List<BaseQuote> baseQuotes = new ArrayList<>();
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.83, LocalDateTime.of(2017, Month.SEPTEMBER, 23, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.86, LocalDateTime.of(2017, Month.SEPTEMBER, 25, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 3.19, LocalDateTime.of(2017, Month.SEPTEMBER, 29, 0, 0, 1)));

        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.42, LocalDateTime.of(2017, Month.OCTOBER, 6, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.6, LocalDateTime.of(2017, Month.OCTOBER, 17, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.76, LocalDateTime.of(2017, Month.OCTOBER, 28, 0, 0, 1)));

        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.76, LocalDateTime.of(2017, Month.NOVEMBER, 3, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.92, LocalDateTime.of(2017, Month.NOVEMBER, 5, 0, 0, 1)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.17, LocalDateTime.of(2017, Month.NOVEMBER, 10, 0, 0, 1)));


        QuotesDao dao = new FakeQuotesDao("RUB/USD", baseQuotes);
        OlsTrendCalculator mnkCalc = new OlsTrendCalculator(dao);
        CalculatorResult result = mnkCalc.calc(newQuote);

        Assert.assertEquals(1.41667, result.getResultForBid(), 0.0001);
        Assert.assertEquals(1.69667, result.getResultForOffer(), 0.0001);
        Assert.assertEquals(1.77218, result.getInaccuracyForBid(), 0.0001);
        Assert.assertEquals(1.72892, result.getInaccuracyForOffer(), 0.0001);
    }

}
