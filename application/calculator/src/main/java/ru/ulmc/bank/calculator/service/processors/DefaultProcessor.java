package ru.ulmc.bank.calculator.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.environment.ResourcesEnvironment;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
@Slf4j
public class DefaultProcessor extends ProcessFunction<IBaseQuote, Quote> {
    private transient CalcService defCalcService;
    private transient CalcService panicCalcService;
    private transient SymbolConfigStorage symbolConfigStorage;


    @Override
    public void open(Configuration parameters) throws Exception {
        ResourcesEnvironment resources = EnvironmentHolder.get();
        defCalcService = resources.getDefaultCalcService();
        symbolConfigStorage = EnvironmentHolder.get().getSymbolConfigStorage();
    }

    @Override
    public void processElement(IBaseQuote quote, Context context, Collector<Quote> collector) throws Exception {
        SymbolConfig symbolConfig = symbolConfigStorage.getSymbolConfig(quote.getSymbol());
        if (symbolConfig == null) {
            log.info("Unknown symbol retrieved - {}. Skipping...", quote.getSymbol());
            return;
        }
        Quote q = defCalcService.calculateQuoteForSymbol(symbolConfig, (BaseQuote) quote);
        collector.collect(q);
    }
}