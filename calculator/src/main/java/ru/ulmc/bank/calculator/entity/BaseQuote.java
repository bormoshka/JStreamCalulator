package ru.ulmc.bank.calculator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id", "symbol", "datetime"})
public class BaseQuote implements Serializable {
    private final String id;
    private final LocalDateTime datetime;
    private final String symbol;
    private final Set<Price> prices;

    public BaseQuote(@NonNull String quoteId, @NonNull LocalDateTime datetime,
                     @NonNull String symbol, @NonNull Set<Price> price) {
        id = quoteId;
        this.datetime = datetime;
        this.symbol = symbol;
        this.prices = new HashSet<>(price);
    }
}
