package ru.ulmc.bank.entities.persistent.financial;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(of = "id")
@Entity
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "FIN_CALC_PRICE",
        indexes = {@Index(name = "CALC_PRICE_VOLUME_INDEX", columnList = "VOL")})
@NoArgsConstructor
@SequenceGenerator(name = "SEQ_CALC_PRICE", allocationSize = 1)
public class CalcPrice implements IPrice /*extends Price */ {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    protected long id;

    @Column(name = "VOL")
    protected int volume;
    @Column(name = "BID")
    protected BigDecimal bid;
    @Column(name = "OFFER")
    protected BigDecimal offer;

    public CalcPrice(int volume, BigDecimal bid, BigDecimal offer) {
        if (volume < 0 || bid == null || offer == null || bid.compareTo(offer) > 0) {
            throw new IllegalArgumentException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }
        this.volume = volume;
        this.bid = bid;
        this.offer = offer;
    }
}
