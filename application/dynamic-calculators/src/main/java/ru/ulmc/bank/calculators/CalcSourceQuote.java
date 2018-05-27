package ru.ulmc.bank.calculators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcSourceQuote implements IBaseQuote {
    @Delegate
    private BaseQuote quote;
    private SymbolConfig symbolConfig;
}
