package ru.ulmc.bank.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public interface IPrice extends Serializable {
    int getVolume();

    BigDecimal getBid();

    BigDecimal getOffer();
}
