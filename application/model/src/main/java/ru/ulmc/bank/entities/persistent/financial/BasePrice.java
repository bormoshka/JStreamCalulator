package ru.ulmc.bank.entities.persistent.financial;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "FIN_BASE_PRICE",
        indexes = {@Index(name = "BASE_PRICE_VOLUME_INDEX", columnList = "VOL")})
@SequenceGenerator(name = "SEQ_BASE_PRICE", allocationSize = 1)
public final class BasePrice extends Price {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private BaseQuote quote;

    public BasePrice(int volume, Double bid, Double offer) {
        super(volume, bid, offer);
    }
}
