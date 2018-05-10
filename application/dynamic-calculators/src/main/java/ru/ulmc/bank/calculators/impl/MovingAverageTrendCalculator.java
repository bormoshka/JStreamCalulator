package ru.ulmc.bank.calculators.impl;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.ulmc.bank.calculators.util.CalcUtils.*;

@CalcPlugin(name = "Вычислитель скользящей средней",
        description = "Скользящее среднее — один из старейших и наиболее распространённый индикатор " +
                "технического анализа, относящийся к трендовым индикаторам")
@NoArgsConstructor
@Slf4j
public class MovingAverageTrendCalculator implements Calculator {
    public static final int ROUNDING_MODE = BigDecimal.ROUND_CEILING;
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final BigDecimal smoothingLevels = new BigDecimal(3);
    private QuotesDao quotesDao;
    private NumberFormat nFormat = new DecimalFormat("0.0000000");
    private int timeSeries = 1;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
    }

    //сглаживание ряда методом скользящих средних, 4 уровня сглаживания
    @Override
    public CalculatorResult calc(CalcSourceQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        SymbolConfig symbolConfig = newQuote.getSymbolConfig();
        List<BaseQuote> statisticQuotes = quotesDao.getLastBaseQuotes(newQuote.getSymbol(), endPeriod.minusDays(timeSeries), endPeriod);
        BaseQuote quote = newQuote.getQuote();
        statisticQuotes.add(quote);
       // statisticQuotes.sort(BaseQuote.DATE_COMPARATOR);

        ArrayList<AverageQuote> smoothingAvgQuotes = getSmoothingAvgQuotes(getAvgQuotes(statisticQuotes));

        int sizeSmoothingQuotes = smoothingAvgQuotes.size();
        int sizeStatisticQuotes = statisticQuotes.size();
        if (sizeSmoothingQuotes < 3 || sizeStatisticQuotes < 3) {
            return new CalculatorResult(getBidForZeroVolume(quote), getOfferForZeroVolume(quote));
        }

        BigDecimal averageQuoteBid = smoothingAvgQuotes.get(sizeSmoothingQuotes - 2).getAverageQuoteBid();
        BigDecimal bidForZeroVolume = getBidForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 1));
        BigDecimal bidForZeroVolumePrev = getBidForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 2));

        BigDecimal forecastBid = calcForecast(averageQuoteBid, bidForZeroVolume, bidForZeroVolumePrev);

        BigDecimal averageQuoteOffer = smoothingAvgQuotes.get(sizeSmoothingQuotes - 2).getAverageQuoteOffer();
        BigDecimal offerForZeroVolume = getOfferForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 1));
        BigDecimal offerForZeroVolumePrev = getOfferForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 2));

        BigDecimal forecastOffer = calcForecast(averageQuoteOffer, offerForZeroVolume, offerForZeroVolumePrev);

        double inaccuracyForBid = calcInaccuracyBid(statisticQuotes, smoothingAvgQuotes);
        double inaccuracyForOffer = calcInaccuracyOffer(statisticQuotes, smoothingAvgQuotes);

        BigDecimal minBaseBid = calcModifiedBid(bidForZeroVolume, symbolConfig.getBidBaseModifier());
        BigDecimal maxBaseBid = calcModifiedBid(bidForZeroVolume, symbolConfig.getBidMaxModifier());

        BigDecimal minBaseOffer = calcModifiedOffer(offerForZeroVolume, symbolConfig.getOfferBaseModifier());
        BigDecimal maxBaseOffer = calcModifiedOffer(offerForZeroVolume, symbolConfig.getOfferMaxModifier());

        BigDecimal forecastBidWithInaccuracy = forecastBid.subtract(forecastBid.multiply(bd(inaccuracyForBid)));
        BigDecimal forecastOfferWithInaccuracy = forecastOffer.add(forecastOffer.multiply(bd(inaccuracyForOffer)));


        log.trace("Calculation {} OFFER | base: {} forecast: {} min: {} forecastInaccurate: {} max: {} ina: {}", symbolConfig.getSymbol(), f(offerForZeroVolume),
                f(forecastOffer), f(minBaseOffer), f(forecastOfferWithInaccuracy), f(maxBaseOffer), f(inaccuracyForOffer));
        log.trace("Calculation {}   BID | base: {} forecast: {} min: {} forecastInaccurate: {} max: {} ina: {}", symbolConfig.getSymbol(), f(bidForZeroVolume),
                f(forecastBid), f(minBaseBid), f(forecastBidWithInaccuracy), f(maxBaseBid), f(inaccuracyForBid));

        if (forecastBidWithInaccuracy.compareTo(minBaseBid) > 0) {
            forecastBid = minBaseBid;
        } else if (forecastBidWithInaccuracy.compareTo(maxBaseBid) < 0) {
            forecastBid = maxBaseBid;
        } else {
            forecastBid = forecastBidWithInaccuracy;
        }

        if (forecastOfferWithInaccuracy.compareTo(minBaseOffer) < 0) {
            forecastOffer = minBaseOffer;
        } else if (forecastOfferWithInaccuracy.compareTo(maxBaseOffer) > 0) {
            forecastOffer = maxBaseOffer;
        } else {
            forecastOffer = forecastOfferWithInaccuracy;
        }

        return new CalculatorResult(forecastBid, forecastOffer);
    }

    private BigDecimal calcForecast(BigDecimal averageQuote,
                                    BigDecimal valueForZeroVolume, BigDecimal valForZeroVolumePrev) {
        return averageQuote
                .add(valueForZeroVolume.subtract(valForZeroVolumePrev).divide(smoothingLevels, ROUNDING_MODE));
    }

    private String f(Number n) {
        return nFormat.format(n);
    }

    private List<AverageQuote> getAvgQuotes(List<BaseQuote> statisticQuotes) {
        List<AverageQuote> statisticAvgQuotes = new ArrayList<>();
        int smLevels = smoothingLevels.intValue();
        for (int i = 0; i < (statisticQuotes.size() - smLevels + 1); i++) {
            BigDecimal sumQuoteBid = BigDecimal.ZERO;
            BigDecimal sumQuoteOffer = BigDecimal.ZERO;

            for (int j = 0; j < smLevels; j++) {
                sumQuoteBid = sumQuoteBid.add(getBidForZeroVolume(statisticQuotes.get(i + j)));
                sumQuoteOffer = sumQuoteOffer.add(getOfferForZeroVolume(statisticQuotes.get(i + j)));
            }

            BaseQuote baseQuote = statisticQuotes.get(i + smLevels - 1);
            statisticAvgQuotes.add(new AverageQuote(baseQuote.getDatetime(), baseQuote.getSymbol(),
                    sumQuoteBid.divide(smoothingLevels, ROUNDING_MODE), sumQuoteOffer.divide(smoothingLevels, ROUNDING_MODE)));
        }
        return statisticAvgQuotes;
    }

    private ArrayList<AverageQuote> getSmoothingAvgQuotes(List<AverageQuote> statisticAvgQuotes) {
        ArrayList<AverageQuote> output = new ArrayList<>();
        int i1 = statisticAvgQuotes.size() - 1;
        for (int i = 0; i < i1; i++) {
            AverageQuote current = statisticAvgQuotes.get(i);
            AverageQuote next = statisticAvgQuotes.get(i + 1);
            output.add(new AverageQuote(next.getOrder(), next.getSymbol(),
                    getAverageOfTwo(current.getAverageQuoteBid(), next.getAverageQuoteBid()),
                    getAverageOfTwo(current.getAverageQuoteOffer(), next.getAverageQuoteOffer())));
        }
        return output;
    }

    private BigDecimal getAverageOfTwo(BigDecimal current, BigDecimal next) {
        return current.add(next).divide(TWO, ROUNDING_MODE);
    }

    private double calcInaccuracyBid(List<BaseQuote> statisticQuotes, ArrayList<AverageQuote> smoothingAvgQuotes) {
        double sumDeviations = 0.0;

        int size = statisticQuotes.size();
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            BigDecimal factValue = getBidForZeroVolume(statisticQuotes.get(i + smoothingLevels.intValue()));
            BigDecimal averageQuoteBid = smoothingAvgQuotes.get(i).getAverageQuoteBid();
            sumDeviations += averageQuoteBid.subtract(factValue).abs()
                    .divide(factValue, ROUNDING_MODE).doubleValue();
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

    private double calcInaccuracyOffer(List<BaseQuote> statisticQuotes, ArrayList<AverageQuote> smoothingAvgQuotes) {
        double sumDeviations = 0.0;
        int size = statisticQuotes.size();
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            BigDecimal factValue = getOfferForZeroVolume(statisticQuotes.get(i + smoothingLevels.intValue()));
            sumDeviations += smoothingAvgQuotes.get(i).getAverageQuoteOffer().subtract(factValue).abs()
                    .divide(factValue, ROUNDING_MODE).doubleValue();
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

}
