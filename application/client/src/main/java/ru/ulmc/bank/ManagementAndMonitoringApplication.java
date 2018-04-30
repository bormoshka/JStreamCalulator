package ru.ulmc.bank;

import org.hibernate.cfg.Environment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import ru.ulmc.bank.config.zookeeper.storage.AppConfigStorage;

import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@ComponentScan("ru.ulmc.bank")
public class ManagementAndMonitoringApplication {

    public static void main(String[] args) throws Exception {
        collectPropertiesFromZookeeper();
        SpringApplication.run(ManagementAndMonitoringApplication.class, args);
    }

    private static void collectPropertiesFromZookeeper() throws Exception {
        AppConfigStorage storage = getStorage();
        Map<String,String> params = storage.getProperties();
        Properties properties = System.getProperties();

        params.forEach(properties::setProperty);
        properties.setProperty("spring.jpa.hibernate.ddl-auto", params.get(Environment.HBM2DDL_AUTO));
        //properties.setProperty("spring.jpa.database-platform", params.get(Environment.PLA));
       // properties.setProperty("spring.jpa.database", params.get(Environment.DATABASE));
        properties.setProperty("spring.datasource.url", params.get(Environment.URL));
        properties.setProperty("spring.datasource.username", params.get(Environment.USER));
        properties.setProperty("spring.datasource.password", params.get(Environment.PASS));
        properties.setProperty("spring.datasource.dialect", params.get(Environment.DIALECT));
        properties.setProperty("spring.datasource.driver-class-name", params.get(Environment.DRIVER));
    }

    private static AppConfigStorage getStorage() throws Exception {
        return new AppConfigStorage(System.getProperty("zookeeper.connectString"));
    }


}

