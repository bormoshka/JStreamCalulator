package ru.ulmc.bank.entities.inner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"symbol", "date"})
public class AverageQuote implements Serializable {
    //private final String id;
    private final LocalDate date;
    private final String symbol;
    private BigDecimal averageQuoteBid;
    private BigDecimal averageQuoteOffer;

    public AverageQuote(LocalDate date, String symbol, BigDecimal averageQuoteBid, BigDecimal averageQuoteOffer) {
        this.date = date;
        this.symbol = symbol;
        this.averageQuoteBid = averageQuoteBid;
        this.averageQuoteOffer = averageQuoteOffer;
    }
}
