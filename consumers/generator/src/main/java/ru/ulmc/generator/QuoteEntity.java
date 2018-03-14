package ru.ulmc.generator;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.bean.IPrice;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class QuoteEntity implements IBaseQuote {
    private String symbol;
    private Set<IPrice> prices = new HashSet<>();
    private LocalDateTime datetime = LocalDateTime.now();
    private String id = UUID.randomUUID().toString();

    public QuoteEntity(String symbol, Double bid, Double offer) {
        this.symbol = symbol;
        prices.add(new IPrice() {
            @Override
            public int getVolume() {
                return 0;
            }

            @Override
            public Double getBid() {
                return bid;
            }

            @Override
            public Double getOffer() {
                return offer;
            }
        });
        prices.add(new IPrice() {
            @Override
            public int getVolume() {
                return 100;
            }

            @Override
            public Double getBid() {
                return bid;
            }

            @Override
            public Double getOffer() {
                return offer;
            }
        });
    }
}
