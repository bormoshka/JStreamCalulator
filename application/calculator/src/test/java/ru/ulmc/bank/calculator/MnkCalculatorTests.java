package ru.ulmc.bank.calculator;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.impl.OlsTrendCalculator;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;
import ru.ulmc.bank.config.zookeeper.storage.AppConfigStorage;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.math.BigDecimal.valueOf;
@Slf4j
public class MnkCalculatorTests {

    @Test
    public void mnkTest() {
        SymbolConfig sc = new SymbolConfig("RUB/USD", 0, 0, 100, 100);
        Set<BasePrice> prices = new HashSet<>();
        prices.add(new BasePrice(0, valueOf(1.42), valueOf(1.52)));
        BaseQuote newQuote = new BaseQuote(UUID.randomUUID().toString(), LocalDateTime.now(), "RUB/USD", prices);

        List<BaseQuote> baseQuotes = new ArrayList<>();
        long sec = 9;
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.83, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.86, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 3.19, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.42, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.6, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.76, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.76, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 1.92, LocalDateTime.now().minus(sec--, ChronoUnit.HOURS)));
        baseQuotes.add(FakeQuotesDao.createBaseQuote("RUB/USD", 2.17, LocalDateTime.now().minus(sec, ChronoUnit.HOURS)));
        QuotesDao dao = new FakeQuotesDao("RUB/USD", baseQuotes);
        OlsTrendCalculator mnkCalc = new OlsTrendCalculator();
        mnkCalc.initialize(new ResourcesEnvironment() {
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

        CalculatorResult result = mnkCalc.calc(new CalcSourceQuote(newQuote, sc));
        Assert.assertEquals(1.39156, result.getResultForBid().doubleValue(), 0.0001);
        Assert.assertEquals(1.72600, result.getResultForOffer().doubleValue(), 0.0001);
        log.info("Test passed!");
    }

}
