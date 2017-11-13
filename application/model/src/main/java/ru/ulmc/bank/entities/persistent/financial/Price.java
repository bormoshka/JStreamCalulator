package ru.ulmc.bank.entities.persistent.financial;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@Entity
public class Price implements Serializable {
    private final int volume;
    private final BigDecimal bid;
    private final BigDecimal offer;

    public Price(int volume, BigDecimal bid, BigDecimal offer) {
        if (volume < 0 || bid == null || offer == null || bid.doubleValue() > offer.doubleValue()) {
            throw new IllegalArgumentException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }

        this.volume = volume;
        this.bid = bid;
        this.offer = offer;
    }
}
