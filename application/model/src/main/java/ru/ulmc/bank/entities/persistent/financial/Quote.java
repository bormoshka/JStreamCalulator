package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
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

    @Column(name = "symbol")
    private String symbol;

    @OneToMany(mappedBy = "quote")
    private Set<CalcPrice> prices;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = BaseQuote.class)
    private BaseQuote sourceQuote;

    protected Quote(@NonNull String id, @NonNull LocalDateTime date,
                    @NonNull String symbol, @NonNull Collection<CalcPrice> prices, @NonNull BaseQuote sourceQuote) {
        this.id = id;
        this.datetime = date;
        this.symbol = symbol;
        this.prices = new HashSet<>(prices);
        this.sourceQuote = sourceQuote;
    }
}
