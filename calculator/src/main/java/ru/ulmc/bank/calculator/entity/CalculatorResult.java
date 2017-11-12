package ru.ulmc.bank.calculator.entity;

import lombok.Data;

@Data
public class CalculatorResult {
    private double resultForBid;
    private double resultForOffer;

    public CalculatorResult(double resultForBid, double resultForOffer) {
        this.resultForBid = resultForBid;
        this.resultForOffer = resultForOffer;
    }
}
