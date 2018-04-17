package ru.ulmc.bank.calculator.environment;

import ru.ulmc.bank.calculator.service.impl.DefaultCalcService;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculators.impl.OlsTrendCalculator;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;

public interface ServiceEnvironment {

    DefaultCalcService getDefaultCalcService();

}
