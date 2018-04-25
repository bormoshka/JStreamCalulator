package ru.ulmc.bank.core.dao;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.core.common.exception.ConfigurationException;

import java.util.Properties;

@Slf4j
public class TransactionManagerHolder {

    private static volatile TransactionManager instance;
    private static Properties properties = null;
    private static HikariDataSource hikariDataSources = null;

    public static void initConfiguration(Properties props) {
        log.trace("Transaction manager initialization");
        if (properties != null) {
            return;
        }
        properties = props;
    }

    public static TransactionManager getInstance() {
        log.trace("Get transaction manager instance");
        TransactionManager localInstance = instance;
        if (localInstance == null) {
            synchronized (TransactionManagerHolder.class) {
                localInstance = instance;
                if (localInstance == null) {
                    log.debug("Initialize transaction manager");
                    if (properties == null) {
                        throw new ConfigurationException("Can't read properties for transaction manager instantiation");
                    }
                    instance = localInstance = new TransactionManager(DatabaseConfigurationFactory.dataSource(properties));
                }
            }
        }
        return localInstance;
    }

}
