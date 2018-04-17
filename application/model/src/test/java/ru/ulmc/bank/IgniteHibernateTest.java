package ru.ulmc.bank;

import org.junit.Test;
import ru.ulmc.bank.dao.impl.IgniteQuotesDao;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

public class IgniteHibernateTest implements Serializable {

    @Test
    public void test() {
        IgniteQuotesDao dao = new IgniteQuotesDao();
        dao.save(new BaseQuote(UUID.randomUUID().toString(), LocalDateTime.now(), "USD/RUB", new HashSet<>()));
    }
}
