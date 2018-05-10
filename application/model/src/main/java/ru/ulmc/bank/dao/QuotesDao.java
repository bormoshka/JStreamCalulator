package ru.ulmc.bank.dao;

import ru.ulmc.bank.entities.inner.ActualQuotes;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Хранилище котировок
 */
public interface QuotesDao {

    void save(BaseQuote quote);

    void save(Quote quote);

    BaseQuote getLastBaseQuote(String symbol);

    List<BaseQuote> getLastBaseQuotes(String symbol, int count);

    List<ActualQuotes> getLastBaseQuotes(Collection<String> symbol);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, int count);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getDailyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getHourlyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getMinutelyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getLastAverageBaseQuotesOnZeroVolume(String symbol, int count);

    Quote getLastCalcQuote(String symbol);

    List<Quote> getLastCalcQuotes(String symbol, int count);

    List<Quote> getLastCalcQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

}
