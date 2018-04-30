package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "symbol", "datetime"})
@Table(name = "FIN_BASE_QUOTE",
        indexes = {@Index(name = "BASE_QUOTE_DATETIME_INDEX", columnList = "datetime"),
                @Index(name = "BASE_QUOTE_SYMBOL_INDEX", columnList = "symbol")})
public class BaseQuote implements IBaseQuote {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "DATETIME")
    private LocalDateTime datetime;

    @Column(name = "DATETIME_RECEIVE")
    private LocalDateTime receiveTime;

    @Column(name = "symbol")
    private String symbol;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "quote_id", nullable = false)
    private Set<BasePrice> prices = new HashSet<>();

    public BaseQuote(@NonNull String quoteId, @NonNull LocalDateTime datetime,
                     @NonNull String symbol, @NonNull Set<BasePrice> price) {
        id = quoteId;
        this.datetime = datetime;
        this.symbol = symbol;
        this.prices.addAll(price);
    }

    public void addPrice(BasePrice bp) {
        prices.add(bp);
    }
}
