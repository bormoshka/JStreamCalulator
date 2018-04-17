package ru.ulmc.bank.calculator.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.transfer.CalculationOutput;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.CalcPrice;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Сервис, отвечающий за вычисление котировок при нормальных отклонениях.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultCalcService implements CalcService {

    @Override
    public Quote calculateQuoteForSymbol(@NonNull SymbolConfig symbolConfig, @NonNull CalculationOutput quotePreResult) {
        //todo: Реализовать вычисление по формуле
        Set<CalcPrice> prices = new HashSet<>();
        IBaseQuote quote = quotePreResult.getQuote();
        quote.getPrices().forEach(bp ->
                prices.add(calc(symbolConfig, bp.getVolume(), bp.getBid(), bp.getOffer())));
        return new Quote(LocalDateTime.now(), symbolConfig.getSymbol(), prices, (BaseQuote) quote);
    }

    private CalcPrice calc(SymbolConfig symbolConfig, int volume, BigDecimal bidBase, BigDecimal offerBase) {
        BigDecimal modBid = calcModifiedBid(bidBase, symbolConfig.getBidBaseModifier());
        BigDecimal modOffer = calcModifiedOffer(offerBase, symbolConfig.getOfferBaseModifier());

        //BigDecimal olsBid = symbolConfig.getCalculators()

        return new CalcPrice(volume, modBid, modOffer);
    }


    private BigDecimal calcModifiedBid(BigDecimal base, double modifier) {
        return base.subtract(base.multiply(BigDecimal.valueOf(modifier)));
    }

    private BigDecimal calcModifiedOffer(BigDecimal base, double modifier) {
        return base.add(base.multiply(BigDecimal.valueOf(modifier)));
    }
}
