package ru.ulmc.bank;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.ulmc.bank.core.dao.JpaQuotesDao;
import ru.ulmc.bank.dao.impl.FakeQuotesDao;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class CoreTest {

    @Test
    public void contextLoads() {
        assertTrue(true);
    }


    @Test
    public void jpaTest() {
        Map<String, String> props = new HashMap<>();
        props.put("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        props.put("hibernate.connection.url", "jdbc:hsqldb:mem:test-in-mem-db");
        props.put("hibernate.connection.username", "sa");
        props.put("hibernate.connection.password", "");
        props.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.hikari.connectionTimeout", "20000");
        props.put("hibernate.hikari.minimumIdle", "10");
        props.put("hibernate.hikari.maximumPoolSize", "20");
        props.put("hibernate.hikari.idleTimeout", "300000");
        props.put("hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
        JpaQuotesDao dao = new JpaQuotesDao(props);
        dao.save(FakeQuotesDao.createBaseQuote("RUB/USD", 50 , LocalDateTime.now().minus(60 * 60 * 6, ChronoUnit.SECONDS)));
        dao.save(FakeQuotesDao.createBaseQuote("RUB/USD", 51 , LocalDateTime.now().minus(60 * 60 * 6, ChronoUnit.SECONDS)));
        dao.save(FakeQuotesDao.createBaseQuote("RUB/USD", 52 , LocalDateTime.now().minus(60 * 60 * 6, ChronoUnit.SECONDS)));
        dao.getDailyAverageBaseQuotesOnZeroVolume("RUB/USD", LocalDateTime.now().minusDays(2), LocalDateTime.now());
        System.out.println(dao.getLastBaseQuote("RUB/USD"));
    }
}
