package ru.ulmc.bank.calculators.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static ru.ulmc.bank.calculators.util.CalcUtils.*;

@CalcPlugin(name = "Динамический калькулятор",
        description = "Вычисление краткосрочной динамики курса. Расширение спреда в сторону тренда")
@NoArgsConstructor
@Slf4j
public class DynamicCalculator implements Calculator {
    private QuotesDao quotesDao;
    private double sensitivityLevel = 0.0001;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
    }

    @Override
    public CalculatorResult calc(CalcSourceQuote newQuote) {
        SymbolConfig sc = newQuote.getSymbolConfig();

        BaseQuote fresh = newQuote.getQuote();
        BigDecimal freshQuoteBid = getBidForZeroVolume(fresh);
        BigDecimal freshQuoteOffer = getOfferForZeroVolume(fresh);
        BigDecimal minBaseBid = calcModifiedBid(freshQuoteBid, sc.getBidBaseModifier());
        BigDecimal minBaseOffer = calcModifiedOffer(freshQuoteOffer, sc.getOfferBaseModifier());

        BaseQuote quote = quotesDao.getLastBaseQuote(sc.getSymbol());

        if (quote != null) {

            BigDecimal oldQuoteBid = getBidForZeroVolume(quote);
            double bidChangePercents = freshQuoteBid.subtract(oldQuoteBid).divide(oldQuoteBid, BigDecimal.ROUND_HALF_UP).doubleValue();
            boolean bidGoesUp = bidChangePercents > 0;

            BigDecimal oldQuoteOffer = getOfferForZeroVolume(quote);
            double offerChangePercents = freshQuoteOffer.subtract(oldQuoteOffer).divide(oldQuoteOffer, BigDecimal.ROUND_HALF_UP).doubleValue();
            boolean offerGoesUp = offerChangePercents > 0;

            BigDecimal maxBaseBid = calcModifiedBid(freshQuoteBid, sc.getBidMaxModifier());

            BigDecimal maxBaseOffer = calcModifiedOffer(freshQuoteOffer, sc.getOfferMaxModifier());

            CalculatorResult calculatorResult;
            if (bidGoesUp && offerGoesUp) { //price is growing
                BigDecimal resultForOffer =
                        calcNewPrice(freshQuoteOffer, offerChangePercents, minBaseOffer, maxBaseOffer, BigDecimal::add);
                calculatorResult = new CalculatorResult(minBaseBid,
                        resultForOffer);
            } else if (!bidGoesUp && !offerGoesUp) { //price is falling
                BigDecimal resultForBid =
                        calcNewPrice(freshQuoteBid, bidChangePercents, maxBaseBid, minBaseBid, BigDecimal::subtract);
                calculatorResult = new CalculatorResult(resultForBid, minBaseOffer);

            } else { //spread changed
                calculatorResult = new CalculatorResult(minBaseBid, minBaseOffer);
            }
            log.trace("Calculation {} result bid {}, offer {} , bc,% {} oc,% {}", sc.getSymbol(), f(calculatorResult.getResultForBid()),
                    f(calculatorResult.getResultForOffer()), f(bidChangePercents), f(offerChangePercents));
            return calculatorResult;
        }
        log.info("No quotes found. using minimal prices");
        return new CalculatorResult(minBaseBid, minBaseOffer);
    }

    private BigDecimal calcNewPrice(BigDecimal currentPrice, double changePercents,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    BiFunction<BigDecimal, BigDecimal, BigDecimal> modifierFunction) {
        BigDecimal calcPrice = modifierFunction.apply(currentPrice, currentPrice.multiply(BigDecimal.valueOf(changePercents)));
        if (isSmallerThan(calcPrice, minPrice)) {
            return minPrice;
        } else if (isGreaterThan(calcPrice, maxPrice)) {
            return maxPrice;
        }
        return calcPrice;
    }
}
