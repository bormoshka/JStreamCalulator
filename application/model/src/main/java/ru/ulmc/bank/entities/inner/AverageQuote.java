package ru.ulmc.bank.entities.inner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"symbol", "datetime"})
public class AverageQuote implements Serializable {
    //private final String id;
    private final LocalDateTime datetime;
    private final String symbol;
    private double averageQuoteBid;
    private double averageQuoteOffer;

}
