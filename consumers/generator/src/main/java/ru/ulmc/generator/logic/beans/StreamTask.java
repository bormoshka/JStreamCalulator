package ru.ulmc.generator.logic.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StreamTask implements Serializable {
    private String symbol;
    private double bid;
    private double offer;
    private double volatility;
    private double interval;

    public QuoteEntity createQuote() {
        return new QuoteEntity(symbol, BigDecimal.valueOf(bid), BigDecimal.valueOf(offer));
    }

}
