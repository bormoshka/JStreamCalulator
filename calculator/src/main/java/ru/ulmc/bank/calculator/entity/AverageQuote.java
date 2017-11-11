package ru.ulmc.bank.calculator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"symbol", "datetime"})
public class AverageQuote implements Serializable {
    //private final String id;
    private final LocalDateTime datetime;
    private final String symbol;
    private BigDecimal averageQuote;

    public AverageQuote(LocalDateTime datetime, String symbol, BigDecimal averageQuote) {
        this.datetime = datetime;
        this.symbol = symbol;
        this.averageQuote = averageQuote;
    }
}
