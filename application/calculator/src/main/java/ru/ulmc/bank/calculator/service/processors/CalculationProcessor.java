package ru.ulmc.bank.calculator.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.service.transfer.CalculationOutput;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.util.CalculatorsLocator;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.config.zookeeper.entities.SymbolCalculatorConfig;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.util.Map;

@Slf4j
public class CalculationProcessor extends ProcessFunction<BaseQuote, CalculationOutput> {
    private transient SymbolConfigStorage symbolConfigStorage;
    private transient Map<String, Calculator> availableCalculators;


    @Override
    public void open(Configuration parameters) {
        symbolConfigStorage = EnvironmentHolder.getResources().getSymbolConfigStorage();
        availableCalculators = CalculatorsLocator.getAvailableCalculators();
    }

    @Override
    public void processElement(BaseQuote quote, Context context, Collector<CalculationOutput> collector) throws Exception {

        SymbolConfig symbolConfig = symbolConfigStorage.getSymbolConfig(quote.getSymbol());

        CalculationOutput calcOutput = new CalculationOutput(quote.getSymbol(), quote);
        Map<String, SymbolCalculatorConfig> calculators = symbolConfig.getCalculators();
        if (calculators != null && !calculators.isEmpty()) {
            calculators.forEach((s, symbolCalculatorConfig) -> {
                if (symbolCalculatorConfig.getBidModifier() != 0.0d || symbolCalculatorConfig.getOfferModifier() != 0.0d) {
                    calcOutput.add(symbolCalculatorConfig, availableCalculators.get(s).calc(new CalcSourceQuote(quote, symbolConfig)));
                }
            });
            collector.collect(calcOutput);
        }
    }

}
