package ru.ulmc.bank.bean;


public interface CurrencyRate {
    Currency getCurrency();

    String getCurrencyCode();

    Currency getBaseCurrency();

    String getBaseCurrencyCode();

    Double getBid();

    Double getAsk();
}
