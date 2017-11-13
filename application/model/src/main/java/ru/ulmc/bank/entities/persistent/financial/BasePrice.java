package ru.ulmc.bank.entities.persistent.financial;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "FIN_BASE_PRICE",
        indexes = {@Index(name = "BASE_PRICE_VOLUME_INDEX", columnList = "volume")})
@SequenceGenerator(name = "SEQ_BASE_PRICE", allocationSize = 1)
public final class BasePrice extends Price {
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private BaseQuote quote;

    public BasePrice(int volume, BigDecimal bid, BigDecimal offer) {
        super(volume, bid, offer);
    }
}
