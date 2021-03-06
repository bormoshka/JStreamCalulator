package ru.ulmc.bank.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IBaseQuote extends Serializable {
    String getSymbol();

    String getId();

    LocalDateTime getDatetime();

    List<? extends IPrice> getPrices();
}
