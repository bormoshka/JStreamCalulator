package ru.ulmc.bank.entities.persistent.financial;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.*;
import java.math.BigDecimal;

@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Getter
@Setter
@Cacheable
@ToString
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "FIN_BASE_PRICE",
        indexes = {@Index(name = "BASE_PRICE_VOLUME_INDEX", columnList = "VOL")})
@SequenceGenerator(name = "SEQ_BASE_PRICE", allocationSize = 1)
public class BasePrice implements IPrice /*extends Price*/ {
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

    public BasePrice(int volume, BigDecimal bid, BigDecimal offer) {
        if (volume < 0 || bid == null || offer == null || bid.compareTo(offer) > 0) {
            throw new IllegalArgumentException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }
        this.volume = volume;
        this.bid = bid;
        this.offer = offer;
    }

}
