package ru.ulmc.bank.core.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "JpaQlInspection"})
public class JpaQuotesDao implements QuotesDao {
    private SessionFactory sessionFactory;

    public JpaQuotesDao(Map<String, String> props) {
        sessionFactory = DatabaseConfigurationFactory.getSessionFactory(props);
    }

    @Override
    public void save(BaseQuote quote) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(quote);
       // quote.getPrices().forEach(session::save);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public BaseQuote getLastBaseQuote(String symbol) {
        return getLastBaseQuotes(symbol, 1).get(0);
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, int count) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<BaseQuote> quote = session.createQuery("select b from BaseQuote b where b.symbol = :symbol order by b.datetime DESC ")
                .setMaxResults(count)
                .setParameter("symbol", symbol)
                .getResultList();
        session.close();
        return quote;
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime) {
        return getLastBaseQuotes(symbol, startDateTime, null);
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        Query<BaseQuote> q = session.createQuery("select b from BaseQuote b " +
                "where b.symbol = :symbol " +
                " and b.datetime >= :dateStart " +
                (endDateTime == null ? "" : " and b.datetime < :dateEnd ") +
                " order by b.datetime DESC ")
                .setParameter("symbol", symbol)
                .setParameter("dateStart", startDateTime);
        if (endDateTime != null) {
            q.setParameter("dateEnd", endDateTime);
        }
        List<BaseQuote> quotes = q.getResultList();
        session.close();
        return quotes;
    }

    @Override
    public List<AverageQuote> getDailyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<Object[]> results = session.createQuery("SELECT cast(b.datetime as date), AVG(p.offer), AVG(p.bid)" +
                " FROM BaseQuote b" +
                " left join b.prices as p" +
                " where b.symbol = :symbol " +
                " and p.volume = 0 " +
                " and b.datetime between :start AND :endDate " +
                " GROUP BY cast(b.datetime as date)")
                .setParameter("symbol", symbol)
                .setParameter("start", startDateTime)
                .setParameter("endDate", endDateTime)
                .getResultList();
        List<AverageQuote> quotes = results.stream().map(objs -> convertToAvg(objs, symbol)).collect(Collectors.toList());
        session.close();
        return quotes;
    }

    private AverageQuote convertToAvg(Object[] fields, String symbol) {
        return new AverageQuote(((java.sql.Date) fields[0]).toLocalDate(),
                symbol, new BigDecimal((Double) fields[1]), new BigDecimal((Double) fields[2]));
    }

    @Override
    public Quote getLastCalcQuote(String symbol) {
        return getLastCalcQuotes(symbol, 1).get(0);
    }

    @Override
    public List<Quote> getLastCalcQuotes(String symbol, int count) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<Quote> quote = session.createQuery("select b from Quote b where b.symbol = :symbol order by b.datetime DESC ")
                .setMaxResults(count)
                .setParameter("symbol", symbol)
                .getResultList();
        session.close();
        return quote;
    }
}
