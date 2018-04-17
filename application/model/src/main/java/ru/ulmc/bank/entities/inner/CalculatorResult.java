package ru.ulmc.bank.entities.inner;

import lombok.Data;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;

@Data
public class CalculatorResult {
    private BigDecimal resultForBid;
    private BigDecimal resultForOffer;
    private double inaccuracyForBid;
    private double inaccuracyForOffer;

    public CalculatorResult(BigDecimal resultForBid, BigDecimal resultForOffer, double inaccuracyForBid, double inaccuracyForOffer) {
        this.resultForBid = resultForBid;
        this.resultForOffer = resultForOffer;
        this.inaccuracyForBid = inaccuracyForBid;
        this.inaccuracyForOffer = inaccuracyForOffer;
    }
}
