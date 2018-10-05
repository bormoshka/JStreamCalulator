package ru.ulmc.bank.entities.inner;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(includeFieldNames = false)
@EqualsAndHashCode
@NoArgsConstructor
public class CalculatorResult implements Serializable {
    private BigDecimal resultForBid;
    private BigDecimal resultForOffer;

    public CalculatorResult(BigDecimal resultForBid, BigDecimal resultForOffer) {
        this.resultForBid = resultForBid;
        this.resultForOffer = resultForOffer;
    }
}
