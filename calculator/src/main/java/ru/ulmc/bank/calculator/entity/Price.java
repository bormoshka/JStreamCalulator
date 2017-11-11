package ru.ulmc.bank.calculator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.ulmc.bank.calculator.exception.ConfigurationException;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public final class Price implements Serializable {
    private final Volume volume;
    private final BigDecimal bid;
    private final BigDecimal offer;

    public Price(int volume, BigDecimal bid, BigDecimal offer) {
        this(new Volume(volume), bid, offer);
    }

    public Price(Volume volume, BigDecimal bid, BigDecimal offer) {
        if (volume == null || bid == null || offer == null || bid.doubleValue() > offer.doubleValue()) {
            throw new ConfigurationException("Illegal arguments: volume = " + volume + " bid=" + bid + " offer=" + offer + ". " +
                    "Some of them are null or bid is greater than offer!");
        }

        this.volume = new Volume(volume.getSize());
        this.bid = bid;
        this.offer = offer;
    }
}
