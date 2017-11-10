package ru.ulmc.bank.calculator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"quoteId", "symbol", "datetime"})
public class Quote implements Serializable {
    private final LocalDateTime datetime;
    private String quoteId;
    private String symbol;
    private Set<Price> prices;
    private BaseQuote sourceQuote;

    protected Quote(@NonNull String quoteId, @NonNull LocalDateTime date,
                    @NonNull String symbol, @NonNull Collection<Price> prices, @NonNull BaseQuote sourceQuote) {
        this.quoteId = quoteId;
        this.datetime = date;
        this.symbol = symbol;
        this.prices = new HashSet<>(prices);
        this.sourceQuote = sourceQuote;
    }
}
