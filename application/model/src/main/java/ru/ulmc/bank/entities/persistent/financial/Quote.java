package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
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

    @Column(name = "SYMBOL")
    private String symbol;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    private Set<CalcPrice> prices;

    @Column(name = "SRC_QUOTE_ID")
    private String baseQuoteId;

    public Quote(@NonNull LocalDateTime date,
                    @NonNull String symbol, @NonNull Collection<CalcPrice> prices, @NonNull BaseQuote sourceQuote) {
        this.id = UUID.randomUUID().toString();
        this.datetime = date;
        this.symbol = symbol;
        this.prices = new HashSet<>(prices);
        this.baseQuoteId = sourceQuote.getId();
    }
}
