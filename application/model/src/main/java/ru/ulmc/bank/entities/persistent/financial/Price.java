package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.ulmc.bank.bean.IPrice;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
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
    protected Double bid;
    @Column(name = "OFFER")
    protected Double offer;

    public Price(int volume, Double bid, Double offer) {
        if (volume < 0 || bid == null || offer == null || bid > offer) {
            throw new IllegalArgumentException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }
        this.volume = volume;
        this.bid = bid;
        this.offer = offer;
    }
}
