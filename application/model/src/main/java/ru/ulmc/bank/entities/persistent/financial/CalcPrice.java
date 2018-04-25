package ru.ulmc.bank.entities.persistent.financial;

import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "FIN_CALC_PRICE",
        indexes = {@Index(name = "CALC_PRICE_VOLUME_INDEX", columnList = "VOL")})
@SequenceGenerator(name = "SEQ_CALC_PRICE", allocationSize = 1)
public class CalcPrice extends Price {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    public CalcPrice(int volume, BigDecimal bid, BigDecimal offer) {
        super(volume, bid, offer);
    }
}
