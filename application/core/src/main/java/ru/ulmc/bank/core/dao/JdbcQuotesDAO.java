package ru.ulmc.bank.core.dao;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.core.common.exception.JdbcException;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class JdbcQuotesDAO {//implements QuotesDao {

  //  private final TransactionManager tm;
//
  //  public JdbcQuotesDAO(TransactionManager transactionManager) {
  //      log.debug("Jdbc quotes DAO initialization");
  //      this.tm = transactionManager;
  //  }
//
//
  //  @Override
  //  public void saveQuotes(@NonNull List<BaseQuote> quotes) {
  //      log.trace("Save base quotes: {}", quotes);
  //      if (!quotes.isEmpty()) {
  //          tm.run((conn) -> runSaveBaseQuoteQuery(conn, entities));
  //      }
  //  }
//
  //  @Override
  //  public BaseQuote getLastFormalizedQuotesForPeriod(@NonNull String symbol, @NonNull Duration duration) {
  //      log.trace("Getting last quote for period {} ", duration);
  //      return tm.get((conn) -> {
  //          BaseQuoteEntity baseQuote = getLastNormalizedBaseQuoteEntityForPeriod(conn, symbol, duration.toMillis());
  //          return QuoteFactory.Base.fromEntity(baseQuote);
  //      }).orElse(null);
  //  }
//
  //  private void runSaveBaseQuoteQuery(Connection conn, List<BaseQuote> entities) {
  //      List<BasePrice> prices = entities.stream()
  //              .map(BaseQuote::getPrices)
  //              .flatMap(Collection::stream)
  //              .collect(toList());
  //      try (PreparedStatement psQuotes = getBaseQuotesPStatement(conn, entities);
  //           PreparedStatement psPrices = getBasePricesPStatemnt(conn, prices)) {
  //          psQuotes.executeBatch();
  //          psPrices.executeBatch();
  //      } catch (SQLException e) {
  //          log.error("Error while saving base quotes");
  //          throw new JdbcException("Ошибка при сохранении базовых котировки", e);
  //      }
  //  }
//
  //  public static PreparedStatement getBaseQuotesPStatement(Connection conn, List<BaseQuoteEntity> entities) throws SQLException {
  //      String baseQuoteInsertString = QuoteType.SOURCE.getInsertQuoteSql();
  //      PreparedStatement ps = conn.prepareStatement(baseQuoteInsertString);
  //      for (BaseQuoteEntity baseQuote : entities) {
  //          fillInsertBaseQuotePStatement(ps, baseQuote);
  //          ps.addBatch();
  //      }
  //      return ps;
  //  }
//
  //  public String getInsertQuoteSql() {
  //      return "" +
  //              " INSERT INTO " + getQuoteTbl() + "(" + getQuoteColumns() + ")" +
  //              " VALUES " + QueryUtils.getPlaceholders(allQuoteColsSize);
  //  }
//
  //  @Override
  //  public void close() {
  //  }
//
//
  //  private QuoteEntity initQuoteEntity(ResultSet rs) {
  //      try {
  //          QuoteEntity quoteEntity = new QuoteEntity(rs.getString("UUID"));
  //          quoteEntity.setQuoted(rs.getString("QUOTED"));
  //          quoteEntity.setBase(rs.getString("BASE"));
  //          quoteEntity.setDateTime(ZonedDateTime
  //                  .ofInstant(rs.getTimestamp("DATETIME").toInstant(), ZoneId.systemDefault()));
  //          quoteEntity.setSymbol(rs.getString("SYMBOL"));
  //          quoteEntity.setTier(rs.getString("TIER"));
  //          quoteEntity.setSourceQuoteUuid(rs.getString("SOURCE_BASE_QUOTE_ID"));
  //          return quoteEntity;
  //      } catch (SQLException e) {
  //          log.error("Error while mapping quote entities");
  //          throw new JdbcException("Ошибка при маппинге котировки из базы данных", e);
  //      }
  //  }
//
  //  private ZonedDateTime getZonedDateTime(ResultSet rs) throws SQLException {
  //      Timestamp timestamp = rs.getTimestamp("START_DATE");
  //      return timestamp == null ? null : ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
  //  }
//
  //  @Override
  //  public void save(BaseQuote quote) {
//
  //  }
//
  //  @Override
  //  public BaseQuote getLastBaseQuote(String symbol) {
  //      return null;
  //  }
//
  //  @Override
  //  public List<BaseQuote> getLastBaseQuotes(String symbol, int count) {
  //      return null;
  //  }
//
  //  @Override
  //  public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime) {
  //      return null;
  //  }
//
  //  @Override
  //  public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
  //      return null;
  //  }
//
  //  @Override
  //  public ArrayList<AverageQuote> getDailyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
  //      return null;
  //  }
//
  //  @Override
  //  public Quote getLastCalcQuote(String symbol) {
  //      return null;
  //  }
//
  //  @Override
  //  public Quote getLastCalcQuote(String symbol, int count) {
  //      return null;
  //  }
}
