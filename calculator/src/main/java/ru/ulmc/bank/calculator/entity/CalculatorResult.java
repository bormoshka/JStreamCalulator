package ru.ulmc.bank.calculator.entity;

import lombok.Data;

@Data
public class CalculatorResult {
    private double resultForBid;
    private double resultForOffer;
    private double inaccuracyForBid;
    private double inaccuracyForOffer;

    public CalculatorResult(double resultForBid, double resultForOffer, double inaccuracyForBid, double inaccuracyForOffer) {
        this.resultForBid = resultForBid;
        this.resultForOffer = resultForOffer;
        this.inaccuracyForBid = inaccuracyForBid;
        this.inaccuracyForOffer = inaccuracyForOffer;
    }
}
