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
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.util.CalculatorsLocator;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CalculationProcessor extends ProcessFunction<IBaseQuote, Set<CalculationOutput>> {
    private transient SymbolConfigStorage symbolConfigStorage;
    private transient Map<String, Calculator> availableCalculators;


    @Override
    public void open(Configuration parameters) {
        symbolConfigStorage = EnvironmentHolder.getResources().getSymbolConfigStorage();
        availableCalculators = CalculatorsLocator.getAvailableCalculators();
    }

    @Override
    public void processElement(IBaseQuote quote, Context context, Collector<Set<CalculationOutput>> collector) throws Exception {
        SymbolConfig symbolConfig = symbolConfigStorage.getSymbolConfig(quote.getSymbol());
        if (symbolConfig == null) {
            log.info("Unknown symbol retrieved - {}. Skipping...", quote.getSymbol());
            return;
        }
        CalculationOutput calcOutput = new CalculationOutput(quote.getSymbol(), quote);
        Set<CalculationOutput> results = new HashSet<>();
        symbolConfig.getCalculators().forEach((s, symbolCalculatorConfig) -> {
            if (symbolCalculatorConfig.getModifier() != 0.0d) {
                calcOutput.add(symbolCalculatorConfig, availableCalculators.get(s).calc((BaseQuote) quote));
            }
        });
        collector.collect(results);
    }
}
