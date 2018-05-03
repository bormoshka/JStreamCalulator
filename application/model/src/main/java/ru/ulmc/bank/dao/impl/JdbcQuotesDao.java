package ru.ulmc.bank.dao.impl;

import org.springframework.orm.hibernate5.HibernateTransactionManager;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcQuotesDao {}/* implements QuotesDao {

    @Override
    public void save(BaseQuote quote) {

    }

    @Override
    public void save(Quote quote) {

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
        return null;
    }

    @Override
    public ArrayList<AverageQuote> getDailyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return null;
    }

    @Override
    public Quote getLastCalcQuote(String symbol) {
        return null;
    }

    @Override
    public List<Quote> getLastCalcQuotes(String symbol, int count) {
        return null;
    }

}*/
