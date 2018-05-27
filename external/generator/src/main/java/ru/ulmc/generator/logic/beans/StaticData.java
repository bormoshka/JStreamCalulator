package ru.ulmc.generator.logic.beans;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.ulmc.generator.logic.QuotesSource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = "uuid")
public class StaticData implements QuotesSource, Serializable {
    public static final double BASE_SPREAD = 0.005;
    private String uuid;
    private String fileName;
    private String symbol;
    private double interval;

    private boolean isActive;


    private List<StaticQuote> quotes;

    public StaticData() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public List<QuoteEntity> getQuotesToPublish() {
        return quotes.stream().map(sq -> new QuoteEntity(symbol, getPrice(sq, true), getPrice(sq, false)))
                .collect(Collectors.toList());
    }

    private BigDecimal getPrice(StaticQuote sq, boolean isBid) {
        return BigDecimal.valueOf(sq.getPrice() + sq.getPrice() * BASE_SPREAD * (isBid ? -1: 1));
    }
}
