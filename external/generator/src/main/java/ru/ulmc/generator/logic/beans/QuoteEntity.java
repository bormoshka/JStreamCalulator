package ru.ulmc.generator.logic.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.bean.IPrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
public class QuoteEntity implements IBaseQuote {
    private String symbol;
    private List<IPrice> prices = new ArrayList<>();
    private LocalDateTime datetime = LocalDateTime.now();
    private String id = UUID.randomUUID().toString();

    public QuoteEntity(String symbol, BigDecimal bid, BigDecimal offer) {
        this.symbol = symbol;
        prices.add(new PriceEntity(0, bid, offer));
        prices.add(new PriceEntity(1000, bid.multiply(BigDecimal.valueOf(1.00001)), offer.multiply(BigDecimal.valueOf(0.9999))));
       // prices.add(new PriceEntity(10000, bid.multiply(BigDecimal.valueOf(1.00199)), offer.multiply(BigDecimal.valueOf(0.99995))));
    }
}
