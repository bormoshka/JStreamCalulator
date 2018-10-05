package ru.ulmc.bank.entities.persistent.financial;

import lombok.*;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public abstract class Price implements IPrice {
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

    public Price(int volume, BigDecimal bid, BigDecimal offer) {
        if (volume < 0 || bid == null || offer == null || bid.compareTo(offer) > 0) {
            throw new IllegalArgumentException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }
        this.volume = volume;
        this.bid = bid;
        this.offer = offer;
    }
}
