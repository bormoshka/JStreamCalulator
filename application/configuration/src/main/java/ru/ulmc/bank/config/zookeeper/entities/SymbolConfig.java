package ru.ulmc.bank.config.zookeeper.entities;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(of = {"symbol"})
@ToString
public class SymbolConfig implements Serializable {
    private String symbol;
    private String base;
    private String quoted;
    private Boolean active = false;
    private double bidBaseModifier;
    private double offerBaseModifier;
    private double bidMaxModifier;
    private double offerMaxModifier;
    private Map<String, SymbolCalculatorConfig> calculators = new HashMap<>();

    private SymbolConfig() {

    }

    public SymbolConfig(String symbol, double bidBaseModifier, double offerBaseModifier, double bidMaxModifier, double offerMaxModifier) {
        this.symbol = symbol;
        this.bidBaseModifier = bidBaseModifier;
        this.offerBaseModifier = offerBaseModifier;
        this.bidMaxModifier = bidMaxModifier;
        this.offerMaxModifier = offerMaxModifier;
    }

    public SymbolConfig(@NonNull String symbol,
                        @NonNull Double bidBaseModifier,
                        @NonNull Double offerBaseModifier) {
        this.bidBaseModifier = bidBaseModifier;
        this.offerBaseModifier = offerBaseModifier;
        setSymbol(symbol);
        setSymbolParts(symbol);

    }

    public void setSymbolParts(@NonNull String symbol) {
        setBase(symbol.substring(0, symbol.indexOf('/')));
        setQuoted(symbol.substring(symbol.indexOf('/') + 1, symbol.length()));
    }

    public Map<String, SymbolCalculatorConfig> getCalculators() {
        if (calculators == null) {
            calculators = new HashMap<>();
        }
        return calculators;
    }
}
