package ru.ulmc.bank.calculator.dao;

import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.AverageQuote;
import ru.ulmc.bank.calculator.entity.Quote;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Хранилище котировок
 */
public interface QuotesDao {

    BaseQuote getLastBaseQuote(String symbol);

    List<BaseQuote> getLastBaseQuotes(String symbol, int count);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime);

    List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<AverageQuote> getDailyAverageBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Quote getLastCalcQuote(String symbol);

    Quote getLastCalcQuote(String symbol, int count);
}
