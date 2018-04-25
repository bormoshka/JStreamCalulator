package ru.ulmc.bank.core.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Price;

import java.util.Map;
import java.util.Properties;

@Slf4j
public class DatabaseConfigurationFactory {

    private static String DB_URL = Environment.URL;
    private static String DB_USER = Environment.USER;
    private static String DB_PASSWORD = "hibernate.connection.password";
    private static String DB_DRIVERCLASS = "hibernate.connection.driver_class";

    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(Map<String, String> props) {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();


                registryBuilder.applySettings(props);

                registry = registryBuilder.build();
                MetadataSources sources = new MetadataSources(registry)
                        .addAnnotatedClass(Price.class)
                        .addAnnotatedClass(BasePrice.class)
                        .addAnnotatedClass(BaseQuote.class);
                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Bean
    public static LocalSessionFactoryBean sessionFactory(Properties properties) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource(properties));
        sessionFactory.setHibernateProperties(properties);
        return sessionFactory;
    }

    @Bean
    public static HibernateTransactionManager transactionManager(Properties properties) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory(properties).getObject());
        return txManager;
    }

    public static HikariDataSource dataSource(Properties properties) {
        log.info("Configuring base datasource with: {}", properties);
        return new HikariDataSource(createBaseConfig(properties));
    }

    private static HikariConfig createBaseConfig(Properties properties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(properties.getProperty(Environment.URL));
        hikariConfig.setUsername(properties.getProperty(Environment.USER));
        hikariConfig.setPassword(properties.getProperty(Environment.PASS));
        hikariConfig.setDriverClassName(properties.getProperty(Environment.DRIVER));
         hikariConfig.setConnectionInitSql("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
       // hikariConfig.setConnectionInitSql("SELECT 1 FROM DUAL");
        hikariConfig.setAutoCommit(true);

        return hikariConfig;
    }
}
