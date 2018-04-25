package ru.ulmc.bank.calculator;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ulmc.bank.dao.repository.QuotesRepository;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataTest {

    @Autowired
    QuotesRepository dao;

    @Test
    public void exampleTest() {
        dao.save(new BaseQuote());
    }

}
