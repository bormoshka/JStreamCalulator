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

    @Column(name = "symbol")
    private String symbol;

    @OneToMany(mappedBy="quote")
    private Set<BasePrice> prices;

    public BaseQuote(@NonNull String quoteId, @NonNull LocalDateTime datetime,
                     @NonNull String symbol, @NonNull Set<BasePrice> price) {
        id = quoteId;
        this.datetime = datetime;
        this.symbol = symbol;
        this.prices = new HashSet<>(price);
    }

    public Set<BasePrice> getPrices() {
        return prices;
    }
}
