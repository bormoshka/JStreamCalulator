package ru.ulmc.bank.calculator.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.serialization.BaseQuoteDto;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class BaseQuoteProcessor extends ProcessFunction<BaseQuoteDto, BaseQuote> {
    private transient SymbolConfigStorage symbolConfigStorage;
    private transient QuotesDao quotesDao;


    @Override
    public void open(Configuration parameters) {
        symbolConfigStorage = EnvironmentHolder.getResources().getSymbolConfigStorage();
        quotesDao = EnvironmentHolder.getResources().getQuotesDao();
    }

    @Override
    public void processElement(BaseQuoteDto iQuote, Context context, Collector<BaseQuote> collector) throws Exception {

        SymbolConfig symbolConfig = symbolConfigStorage.getSymbolConfig(iQuote.getSymbol());
        if (symbolConfig == null) {
            log.info("Unknown symbol retrieved - {}. Skipping...", iQuote.getSymbol());
            return;
        }
        try {
            BaseQuote quote = convert(iQuote);
            collector.collect(quote);
            CompletableFuture.runAsync(() -> quotesDao.save(quote));
        } catch (Exception ex) {
            log.error("Processing BaseQuote Error", ex);
            log.info("Skipping BaseQuote processing");
        }

    }

    private BaseQuote convert(BaseQuoteDto ibq) {
        BaseQuote bq = new BaseQuote();
        bq.setDatetime(ibq.getDatetime());
        bq.setReceiveTime(LocalDateTime.now());
        bq.setSymbol(ibq.getSymbol());
        bq.setId(UUID.randomUUID().toString());
        ibq.getPrices().forEach(p -> bq.addPrice(new BasePrice(p.getVolume(), p.getBid(), p.getOffer())));
        log.info(" Saving external IBaseQuote with ID {} to DB with ID {}", ibq.getId(), bq.getId());
        return bq;
    }
}
