package ru.ulmc.bank.calculator.dao;

import ru.ulmc.bank.calculator.entity.BaseQuote;
import ru.ulmc.bank.calculator.entity.Quote;

import java.time.LocalDateTime;

/**
 * Хранилище котировок
 */
public interface QuotesDao {

    BaseQuote getLastBaseQuote(String symbol);

    BaseQuote getLastBaseQuotes(String symbol, int count);

    BaseQuote getLastBaseQuotes(String symbol, LocalDateTime startDateTime);

    BaseQuote getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Quote getLastCalcQuote(String symbol);

    Quote getLastCalcQuote(String symbol, int count);
}
