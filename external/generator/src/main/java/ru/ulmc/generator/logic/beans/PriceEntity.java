package ru.ulmc.generator.logic.beans;

import lombok.*;
import ru.ulmc.bank.bean.IPrice;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PriceEntity implements IPrice {
    private int volume;
    private BigDecimal bid;
    private BigDecimal offer;

}
