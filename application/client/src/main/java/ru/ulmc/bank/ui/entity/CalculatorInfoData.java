package ru.ulmc.bank.ui.entity;

import lombok.Getter;
import lombok.experimental.Delegate;
import ru.ulmc.bank.calculators.util.CalculatorInfo;
import ru.ulmc.bank.config.zookeeper.entities.SymbolCalculatorConfig;

import java.util.regex.Pattern;

@Getter
public class CalculatorInfoData {
    private String symbol;
    private final String name;
    private final String description;
    private final String className;
    private final String fullClassName;
    @Delegate
    private SymbolCalculatorConfig calculatorConfig;
    private String bidModifierStr;
    private String offerModifierStr;
    private static final String regexp = "\\d(\\.\\d{1,5})?";
    private Pattern pat = Pattern.compile(regexp);

    public CalculatorInfoData(String symbol, CalculatorInfo calculatorInfo, SymbolCalculatorConfig calculatorConfig) {
        this.symbol = symbol;
        this.name = calculatorInfo.getName();
        this.description = calculatorInfo.getDescription();
        this.className = calculatorInfo.getClassName();
        this.fullClassName = calculatorInfo.getFullClassName();
        this.calculatorConfig = calculatorConfig;
        bidModifierStr = String.valueOf(calculatorConfig.getBidModifier());
        offerModifierStr = String.valueOf(calculatorConfig.getOfferModifier());
    }

    public void setBidModifierStr(String str) {
        this.bidModifierStr = str;
        if (str != null && !str.isEmpty() && pat.matcher(str).matches()) {
            calculatorConfig.setBidModifier(Double.parseDouble(str));
        }
    }
    public void setOfferModifierStr(String str) {
        this.offerModifierStr = str;
        if (str != null && !str.isEmpty() && pat.matcher(str).matches()) {
            calculatorConfig.setOfferModifier(Double.parseDouble(str));
        }
    }
}
