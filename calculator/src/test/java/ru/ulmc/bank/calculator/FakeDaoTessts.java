package ru.ulmc.bank.calculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ulmc.bank.calculator.dao.QuotesDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan("ru.ulmc")
public class FakeDaoTessts {
    @Autowired
    QuotesDao dao;

    @Test
    public void contextLoads() {
        //nothing to do
        dao.getLastBaseQuote(null);
    }
}
