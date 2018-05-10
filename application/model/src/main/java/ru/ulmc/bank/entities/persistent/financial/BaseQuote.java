package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private List<BasePrice> prices = new ArrayList<>();

    public static final Comparator<BaseQuote> DATE_COMPARATOR = (o1, o2) -> {
        if (o1.getDatetime().isAfter(o2.getDatetime())) {
            return 1;
        } else if (o1.getDatetime().isBefore(o2.getDatetime())) {
            return -1;
        }
        return 0;
    };

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
