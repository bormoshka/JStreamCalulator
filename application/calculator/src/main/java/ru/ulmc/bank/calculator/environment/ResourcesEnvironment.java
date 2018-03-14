package ru.ulmc.bank.calculator.environment;

import ru.ulmc.bank.calculator.service.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculator.service.calculators.impl.OlsTrendCalculator;
import ru.ulmc.bank.calculator.service.impl.DefaultCalcService;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;

public interface ResourcesEnvironment {

    QuotesDao getQuotesDao();

    DynamicCalculator getDynamicCalculator();

    OlsTrendCalculator getOlsTrendCalculator();

    DefaultCalcService getDefaultCalcService();

    SymbolConfigStorage getSymbolConfigStorage();
}
