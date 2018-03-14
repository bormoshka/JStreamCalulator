package ru.ulmc.bank.calculator.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.calculators.impl.DynamicCalculator;
import ru.ulmc.bank.calculator.service.calculators.impl.OlsTrendCalculator;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.CalcPrice;
import ru.ulmc.bank.entities.persistent.financial.Price;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сервис, отвечающий за вычисление котировок при нормальных отклонениях.
 */
@Slf4j
@Component
public class DefaultCalcService implements CalcService {
    private final DynamicCalculator dynamicCalculator;
    private final OlsTrendCalculator olsTrendCalculator;

    @Autowired
    public DefaultCalcService(DynamicCalculator dynamicCalculator,
                              OlsTrendCalculator olsTrendCalculator) {
        this.dynamicCalculator = dynamicCalculator;
        this.olsTrendCalculator = olsTrendCalculator;
    }

    @Override
    public Quote calculateQuoteForSymbol(@NonNull SymbolConfig symbolConfig,@NonNull BaseQuote newQuote) {
        //todo: Реализовать вычисление по формуле
        Set<CalcPrice> prices = new HashSet<>();
        newQuote.getPrices().forEach(bp -> prices.add(calc(bp.getVolume(), bp.getBid(), bp.getOffer(),
                symbolConfig.getBidBaseModifier(), symbolConfig.getOfferBaseModifier())));
        return new Quote(LocalDateTime.now(), symbolConfig.getSymbol(), prices, newQuote);
    }

    private CalcPrice calc(int volume, double bidBase, double offerBase, double b, double o) {
        double bid = bidBase - bidBase * b;
        double offer = offerBase + bidBase * o;
        return new CalcPrice(volume, bid, offer);
    }
}
