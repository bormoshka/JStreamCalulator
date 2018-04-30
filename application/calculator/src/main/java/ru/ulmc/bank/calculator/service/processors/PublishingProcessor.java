package ru.ulmc.bank.calculator.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.environment.ServiceEnvironment;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.transfer.CalculationOutput;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class PublishingProcessor extends ProcessFunction<Quote, Quote> {
    private transient SymbolConfigStorage symbolConfigStorage;
    private QuotesDao dao;

    @Override
    public void open(Configuration parameters) throws Exception {
        ServiceEnvironment resources = EnvironmentHolder.getServices();
        dao = EnvironmentHolder.getResources().getQuotesDao();
    }

    @Override
    public void processElement(Quote quote, Context context, Collector<Quote> collector) throws Exception {
        log.info("Publishing quote {}", quote);
        CompletableFuture.runAsync(() -> dao.save(quote));
    }
}
