package ru.ulmc.bank.bean;

import java.io.Serializable;

public interface IPrice extends Serializable {
    int getVolume();

    Double getBid();

    Double getOffer();
}
