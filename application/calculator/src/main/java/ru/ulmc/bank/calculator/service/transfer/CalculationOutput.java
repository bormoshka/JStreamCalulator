package ru.ulmc.bank.calculator.service.transfer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.config.zookeeper.entities.SymbolCalculatorConfig;
import ru.ulmc.bank.entities.inner.CalculatorResult;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CalculationOutput {
    private final String symbol;
    private final IBaseQuote quote;
    private Map<SymbolCalculatorConfig, CalculatorResult> calculatorResult = new HashMap<>();

    public void add(SymbolCalculatorConfig key, CalculatorResult val) {
        calculatorResult.put(key, val);
    }
}
