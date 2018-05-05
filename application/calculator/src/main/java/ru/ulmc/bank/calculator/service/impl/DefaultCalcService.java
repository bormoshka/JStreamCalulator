package ru.ulmc.bank.calculator.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.calculator.service.CalcService;
import ru.ulmc.bank.calculator.service.transfer.CalculationOutput;
import ru.ulmc.bank.entities.configuration.SymbolCalculatorConfig;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.CalcPrice;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.DoubleStream;

/**
 * Сервис, отвечающий за вычисление котировок при нормальных отклонениях.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultCalcService implements CalcService {

    @Override
    public Quote calculateQuoteForSymbol(@NonNull SymbolConfig symbolConfig, @NonNull CalculationOutput quotePreResult) {
        Set<CalcPrice> prices = new HashSet<>();
        IBaseQuote quote = quotePreResult.getQuote();
        quote.getPrices().forEach(bp ->
                prices.add(calc(symbolConfig, quotePreResult, bp.getVolume(), bp.getBid(), bp.getOffer())));
        return new Quote(LocalDateTime.now(), symbolConfig.getSymbol(), prices, (BaseQuote) quote);
    }

    private CalcPrice calc(SymbolConfig symbolConfig, CalculationOutput quotePreResult, int volume, BigDecimal bidBase, BigDecimal offerBase) {
        List<BigDecimal> bids = new ArrayList<>();
        List<BigDecimal> offers = new ArrayList<>();
        List<Double> bidModifiers = new ArrayList<>();
        List<Double> offerModifiers = new ArrayList<>();

        // minimal bid and offer
        Double bidBaseModifier = symbolConfig.getBidBaseModifier();
        bids.add(calcModifiedBid(bidBase, bidBaseModifier));
        bidModifiers.add(1d);

        Double offerBaseModifier = symbolConfig.getOfferBaseModifier();
        offers.add(calcModifiedOffer(offerBase, offerBaseModifier));
        offerModifiers.add(1d);

        quotePreResult.getCalculatorResult().forEach((calculatorConfig, calcRes) -> {
            SymbolCalculatorConfig calcConf = symbolConfig.getCalculators().get(calculatorConfig.getFullClassname());
            double bidModifier = calcConf.getBidModifier();
            bids.add(calcRes.getResultForBid().multiply(BigDecimal.valueOf(bidModifier)));
            bidModifiers.add(bidModifier);

            double offerModifier = calcConf.getOfferModifier();
            offers.add(calcRes.getResultForOffer().multiply(BigDecimal.valueOf(offerModifier)));
            offerModifiers.add(offerModifier);
         //   log.trace("Calculated data {} {} {}", symbolConfig.getSymbol(), );
        });
        Result bid = new Result();
        bids.forEach(bid::add);
        Result offer = new Result();
        offers.forEach(offer::add);

        return new CalcPrice(volume, bid.divideOn(bidModifiers), offer.divideOn(offerModifiers));
    }


    private BigDecimal calcModifiedBid(BigDecimal base, double modifier) {
        return base.subtract(base.multiply(BigDecimal.valueOf(modifier)));
    }

    private BigDecimal calcModifiedOffer(BigDecimal base, double modifier) {
        return base.add(base.multiply(BigDecimal.valueOf(modifier)));
    }

    private class Result {
        private BigDecimal value = BigDecimal.ZERO;

        void add(BigDecimal dec) {
            value = value.add(dec);
        }

        BigDecimal divideOn(List<Double> modifiers) {
            return value.divide(BigDecimal.valueOf(modifiers.stream()
                    .flatMapToDouble(d -> DoubleStream.of(d.doubleValue())).sum()), BigDecimal.ROUND_HALF_UP);
        }
    }
}
