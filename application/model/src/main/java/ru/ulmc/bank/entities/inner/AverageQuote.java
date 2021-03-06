package ru.ulmc.bank.entities.inner;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"symbol", "order"})
public class AverageQuote implements Serializable, Comparable<AverageQuote> {
    //private final String id;
    private final LocalDateTime order;
    private final String symbol;
    private BigDecimal averageQuoteBid;
    private BigDecimal averageQuoteOffer;

    public AverageQuote(LocalDateTime date, String symbol, BigDecimal averageQuoteBid, BigDecimal averageQuoteOffer) {
        this.order = date;
        this.symbol = symbol;
        this.averageQuoteBid = averageQuoteBid;
        this.averageQuoteOffer = averageQuoteOffer;
    }

    @Override
    public int compareTo(@NonNull AverageQuote o) {
        return order.compareTo(o.getOrder());
    }
}
