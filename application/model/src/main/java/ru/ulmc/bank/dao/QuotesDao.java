package ru.ulmc.bank.dao;

import org.springframework.data.repository.Repository;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище котировок
 */
public interface QuotesDao {

    void save(BaseQuote quote);

    void save(Quote quote);

    BaseQuote getLastBaseQuote(String symbol);

    List<BaseQuote> getLastBaseQuotes(String symbol, int count);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getDailyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Quote getLastCalcQuote(String symbol);

    List<Quote> getLastCalcQuotes(String symbol, int count);

}
