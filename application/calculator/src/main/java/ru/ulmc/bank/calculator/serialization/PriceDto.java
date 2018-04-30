package ru.ulmc.bank.calculator.serialization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ulmc.bank.bean.IPrice;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PriceDto implements IPrice {
    private int volume;
    private BigDecimal bid;
    private BigDecimal offer;
}
