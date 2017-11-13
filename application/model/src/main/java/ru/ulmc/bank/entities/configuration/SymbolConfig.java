package ru.ulmc.bank.entities.configuration;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Data
public class SymbolConfig implements Serializable {
    private String symbol;
    private String base;
    private String quoted;
    private Boolean active;
    private Set<String> calculators;

    private SymbolConfig() {
    }

    public SymbolConfig(String symbol) {
        Objects.requireNonNull(symbol);
        setSymbol(symbol);
        setSymbolParts(symbol);
    }

    public void setSymbolParts(String symbol) {
        Objects.requireNonNull(symbol);
        setBase(symbol.substring(0, symbol.indexOf('/')));
        setQuoted(symbol.substring(symbol.indexOf('/') + 1, symbol.length()));
    }
}
