package ru.ulmc.bank.calculator.serialization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ulmc.bank.bean.IBaseQuote;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class BaseQuoteDto implements IBaseQuote {
    private String symbol;
    private List<PriceDto> prices;
    private LocalDateTime datetime;
    private String id;
}
