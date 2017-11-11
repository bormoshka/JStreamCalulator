package ru.ulmc.bank.calculator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.dao.impl.FakeQuotesDao;
import ru.ulmc.bank.calculator.entity.AverageQuote;
import ru.ulmc.bank.calculator.entity.BaseQuote;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("ru.ulmc")
public class FakeDaoTests {
    @Autowired
    QuotesDao dao;

    @Test
    public void averageTest() {
        List<BaseQuote> baseQuotes = new ArrayList<>();
        LocalDateTime date = LocalDateTime.of(2017, Month.OCTOBER, 10, 0, 0, 0);
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 40, LocalDateTime.of(2017, Month.OCTOBER, 10, 0, 0, 1)));
        for (int i = 0; i < 11; i++) {
            baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 50 + i, date.minus(i * 60 * 60 * 6, ChronoUnit.SECONDS)));
        }


        QuotesDao dao = new FakeQuotesDao("RUB/USD", baseQuotes);

        List<AverageQuote> avgs = dao.getDailyAverageBaseQuotes("RUB/USD", LocalDateTime.of(2017, Month.SEPTEMBER, 10, 0, 0, 1), LocalDateTime.now());
        Assert.assertNotNull(baseQuotes);
        Assert.assertNotNull(dao);
        Assert.assertEquals(4, avgs.size());
        Assert.assertEquals(56.3, avgs.get(2).getAverageQuoteBid(), 0.0001);
        Assert.assertEquals(44.8, avgs.get(0).getAverageQuoteBid(), 0.0001);
        Assert.assertEquals(52.7, avgs.get(1).getAverageQuoteOffer(), 0.0001);
        Assert.assertEquals(59.7, avgs.get(3).getAverageQuoteOffer(), 0.0001);
    }

    @Test
    public void contextLoads() {
        //nothing to do
        dao.getLastBaseQuote(null);
    }
}
