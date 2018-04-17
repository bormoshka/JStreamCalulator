package ru.ulmc.bank.entities.configuration;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
public class SymbolConfig implements Serializable {
    private String symbol;
    private String base;
    private String quoted;
    private Boolean active;
    private Double bidBaseModifier;
    private Double offerBaseModifier;
    private Map<String, SymbolCalculatorConfig> calculators;

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
}
