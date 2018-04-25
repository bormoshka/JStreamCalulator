package ru.ulmc.bank.entities.persistent.financial;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "FIN_BASE_PRICE",
        indexes = {@Index(name = "BASE_PRICE_VOLUME_INDEX", columnList = "VOL")})
@SequenceGenerator(name = "SEQ_BASE_PRICE", allocationSize = 1)
public class BasePrice extends Price {
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private transient BaseQuote quote;

    public BasePrice(int volume, BigDecimal bid, BigDecimal offer) {
        super(volume, bid, offer);
    }

    public BasePrice(int volume, BigDecimal bid, BigDecimal offer, BaseQuote quote) {
        super(volume, bid, offer);
        this.quote = quote;
    }
}
