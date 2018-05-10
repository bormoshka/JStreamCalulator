package ru.ulmc.bank.core.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.ActualQuotes;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "JpaQlInspection"})
@Service
public class JpaQuotesDao implements QuotesDao {
    private SessionFactory sessionFactory;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public JpaQuotesDao(EntityManagerFactory factory) {
        this.sessionFactory = factory.unwrap(SessionFactory.class);
        //   this(System.getProperties().entrySet().stream().collect(Collectors.toMap(o -> (String) o.getKey(), o -> (String) o.getValue())));
    }

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
    public void save(Quote quote) {
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
    public List<ActualQuotes> getLastBaseQuotes(Collection<String> symbol) {
        List<ActualQuotes> quotes = new ArrayList<>(symbol.size());
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();

        List<BaseQuote> baseQuotes = session.createQuery("select distinct b from BaseQuote b " +
         //       " join fetch BasePrice " +
                " where b.symbol in :symbol " +
                " and b.datetime = (select max(d.datetime) from BaseQuote d where d.symbol = b.symbol) " +
                " order by b.symbol DESC ")
                .setParameter("symbol", symbol)
                .getResultList();

        List<Quote> calcQuotes = session.createQuery("select distinct q from Quote q " +
          //      " join fetch CalcPrice cp " +
                " where q.symbol in :symbol " +
                " and q.datetime = (select max(d.datetime) from Quote d where d.symbol = q.symbol) " +
                " order by q.symbol DESC ")
                .setParameter("symbol", symbol)
                .getResultList();
        session.close();

        baseQuotes.forEach(quote -> {
            Quote calcQuote = calcQuotes.stream().filter(cq -> cq.getSymbol().equalsIgnoreCase(quote.getSymbol())).findFirst().orElse(null);
            quotes.add(new ActualQuotes(quote.getSymbol(), quote, calcQuote));
        });

        return quotes;
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime) {
        return getLastBaseQuotes(symbol, startDateTime, null);
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, int count) {
        return getLastBaseQuotes(symbol, startDateTime, null, count);
    }

    @Override
    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        Query<BaseQuote> q = session.createQuery("select b from BaseQuote b " +
                "where b.symbol = :symbol " +
                " and b.datetime >= :dateStart " +
                (endDateTime == null ? "" : " and b.datetime < :dateEnd ") +
                " order by b.datetime ASC ")
                .setParameter("symbol", symbol)
                .setParameter("dateStart", startDateTime);
        if (endDateTime != null) {
            q.setParameter("dateEnd", endDateTime);
        }
        List<BaseQuote> quotes = q.getResultList();
        session.close();
        return quotes;
    }

    public List<BaseQuote> getLastBaseQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer count) {
        Session session = sessionFactory.openSession();
        Query<BaseQuote> q = session.createQuery("select b from BaseQuote b " +
                "where b.symbol = :symbol " +
                " and b.datetime >= :dateStart " +
                (endDateTime == null ? "" : " and b.datetime < :dateEnd ") +
                " order by b.datetime DESC ")
                .setParameter("symbol", symbol)
                .setParameter("dateStart", startDateTime);
        if(count != null) {
            q.setMaxResults(count);
        }
        if (endDateTime != null) {
            q.setParameter("dateEnd", endDateTime);
        }
        List<BaseQuote> quotes = q.getResultList();
        quotes.sort(BaseQuote.DATE_COMPARATOR);
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

    @Override
    public List<AverageQuote> getHourlyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<Object[]> results = session.createQuery("SELECT b.datetime, AVG(p.offer), AVG(p.bid), hour(b.datetime)" +
                " FROM BaseQuote b" +
                " left join b.prices as p" +
                " where b.symbol = :symbol " +
                " and p.volume = 0 " +
                " and b.datetime between :start AND :endDate " +
                " GROUP BY hour(b.datetime)")
                .setParameter("symbol", symbol)
                .setParameter("start", startDateTime)
                .setParameter("endDate", endDateTime)
                .getResultList();
        List<AverageQuote> quotes = results.stream().map(objs -> convertToAvg(objs, symbol)).collect(Collectors.toList());
        session.close();
        return quotes;
    }

    @Override
    public List<AverageQuote> getMinutelyAverageBaseQuotesOnZeroVolume(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<Object[]> results = session.createQuery("SELECT function('TO_CHAR', b.datetime, 'yyyy-mm-dd HH24:mi') as minTime, AVG(p.offer), AVG(p.bid) " +
                " FROM BaseQuote b" +
                " left join b.prices as p" +
                " where b.symbol = :symbol " +
                " and p.volume = 0 " +
                " and b.datetime between :start AND :endDate " +
                " GROUP BY function('TO_CHAR', b.datetime, 'yyyy-mm-dd HH24:mi') Order by minTime")
                .setParameter("symbol", symbol)
                .setParameter("start", startDateTime)
                .setParameter("endDate", endDateTime)
                .getResultList();
        List<AverageQuote> quotes = results.stream().map(objs -> convertToAvgStringDate(objs, symbol)).collect(Collectors.toList());
        session.close();
        return quotes;
    }

    @Override
    public List<AverageQuote> getLastAverageBaseQuotesOnZeroVolume(String symbol, int count) {
        Session session = sessionFactory.openSession();
        sessionFactory.createEntityManager();
        List<Object[]> results = session.createQuery("SELECT b.datetime, p.offer, p.bid " +
                " FROM BaseQuote b" +
                " left join b.prices as p" +
                " where b.symbol = :symbol " +
                " and p.volume = 0 " +
                "  Order by b.datetime DESC")
                .setParameter("symbol", symbol)
                .setMaxResults(count)
                .getResultList();
        List<AverageQuote> quotes = results.stream().map(objs -> convertToAvgTimestamp(objs, symbol)).collect(Collectors.toList());
        session.close();
        Collections.sort(quotes);
        return quotes;
    }

    private AverageQuote convertToAvg(Object[] fields, String symbol) {
        return new AverageQuote(((java.sql.Date) fields[0]).toLocalDate().atStartOfDay(),
                symbol, new BigDecimal((Double) fields[1]), new BigDecimal((Double) fields[2]));
    }

    private AverageQuote convertToAvgTimestamp(Object[] fields, String symbol) {
        return new AverageQuote((LocalDateTime) fields[0],
                symbol, (BigDecimal) fields[1], (BigDecimal) fields[2]);
    }

    private AverageQuote convertToAvgStringDate(Object[] fields, String symbol) {
        return new AverageQuote(
                LocalDateTime.parse((String) fields[0], dateTimeFormatter),
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
        List<Quote> quote = session.createQuery("select b from Quote b where b.symbol = :symbol order by b.baseDateTime ASC ")
                .setMaxResults(count)
                .setParameter("symbol", symbol)
                .getResultList();
        session.close();
        return quote;
    }

    @Override
    public List<Quote> getLastCalcQuotes(String symbol, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Session session = sessionFactory.openSession();
        Query<Quote> q = session.createQuery("select b from Quote b " +
                "where b.symbol = :symbol " +
                " and b.baseDateTime >= :dateStart " +
                (endDateTime == null ? "" : " and b.baseDateTime < :dateEnd ") +
                " order by b.baseDateTime ASC ")
                .setParameter("symbol", symbol)
                .setParameter("dateStart", startDateTime);
        if (endDateTime != null) {
            q.setParameter("dateEnd", endDateTime);
        }
        List<Quote> quotes = q.getResultList();
        session.close();
        return quotes;
    }
}
