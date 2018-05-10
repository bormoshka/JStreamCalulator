package ru.ulmc.bank.entities.inner;

import lombok.*;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "symbol")
public class ActualQuotes implements Serializable {
    private String symbol;
    private BaseQuote baseQuote;
    private Quote calcQuote;
}
