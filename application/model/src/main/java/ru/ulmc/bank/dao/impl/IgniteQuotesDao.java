package ru.ulmc.bank.dao.impl;

import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IgniteQuotesDao implements QuotesDao {

    private final EntityManagerFactory emf;

    public IgniteQuotesDao() {
        emf = Persistence.createEntityManagerFactory("ogm-jpa-ignite");
    }

    @Override
    public void save(BaseQuote quote) {
        EntityManager manager = emf.createEntityManager();
        manager.merge(quote);
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
    public ArrayList<AverageQuote> getDailyAverageBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
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
