package ru.ulmc.bank.calculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ulmc.bank.calculator.service.impl.DefaultCalcService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultCalcServiceTests {
    @Autowired
    private DefaultCalcService calcService;

    @Test
    public void testCalculation() {
        //todo: test code
    }
}
