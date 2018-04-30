package ru.ulmc.bank.entities.configuration;

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
    private Double bidBaseModifier;
    private Double offerBaseModifier;
    private Map<String, SymbolCalculatorConfig> calculators = new HashMap<>();

    private SymbolConfig() {

    }

    public SymbolConfig(@NonNull String symbol, @NonNull Double bidBaseModifier, @NonNull Double offerBaseModifier) {
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
