package ru.ulmc.bank.dao;

import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
import ru.ulmc.bank.entities.inner.AverageQuote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Хранилище котировок
 */
public interface QuotesDao {

    BaseQuote getLastBaseQuote(String symbol);

    List<BaseQuote> getLastBaseQuotes(String symbol, int count);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    ArrayList<AverageQuote> getDailyAverageBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Quote getLastCalcQuote(String symbol);

    Quote getLastCalcQuote(String symbol, int count);

}
