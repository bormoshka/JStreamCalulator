package ru.ulmc.bank.calculators;

import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;

public interface ResourcesEnvironment {

    QuotesDao getQuotesDao();

    SymbolConfigStorage getSymbolConfigStorage();
}
