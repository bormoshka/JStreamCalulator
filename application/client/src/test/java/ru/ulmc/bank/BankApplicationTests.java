package ru.ulmc.bank;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.ulmc.bank.dao.repository.UserRepository;

import static org.junit.Assert.assertTrue;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@ActiveProfiles("test")
public class BankApplicationTests {

    // @Autowired
    UserRepository repository;

    //@Test
    public void contextLoads() {
        assertTrue(true);
    }

   // @Test
    public void contextLoads2() {
        assertTrue(true);
    }

}
