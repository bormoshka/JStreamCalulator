package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@EqualsAndHashCode(of = {"id", "symbol", "datetime"})
@Table(name = "FIN_CALC_QUOTE",
        indexes = {@Index(name = "CALC_QUOTE_DATETIME_INDEX", columnList = "datetime"),
                @Index(name = "CALC_QUOTE_SYMBOL_INDEX", columnList = "symbol")})
@SequenceGenerator(name = "SEQ_CALC_QUOTE", allocationSize = 1)
public class Quote implements Serializable {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "DATETIME")
    private LocalDateTime datetime;

    @Column(name = "BASE_DATETIME")
    private LocalDateTime baseDateTime;

    @Column(name = "SYMBOL")
    private String symbol;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private List<CalcPrice> prices;

    @Column(name = "SRC_QUOTE_ID")
    private String baseQuoteId;

    public Quote(@NonNull LocalDateTime date,
                    @NonNull String symbol, @NonNull Collection<CalcPrice> prices, @NonNull BaseQuote sourceQuote) {
        this.id = UUID.randomUUID().toString();
        this.datetime = date;
        this.symbol = symbol;
        this.prices = new ArrayList<>(prices);
        baseDateTime = sourceQuote.getDatetime();
        this.baseQuoteId = sourceQuote.getId();
    }
}
