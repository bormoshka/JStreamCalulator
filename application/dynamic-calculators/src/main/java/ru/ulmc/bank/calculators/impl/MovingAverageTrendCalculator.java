package ru.ulmc.bank.calculators.impl;


import lombok.NoArgsConstructor;
import ru.ulmc.bank.bean.IPrice;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.core.common.exception.FxException;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Price;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CalcPlugin(name = "Вычислитель скользящей средней",
        description = "Скользящее среднее — один из старейших и наиболее распространённый индикатор " +
        "технического анализа, относящийся к трендовым индикаторам")
@NoArgsConstructor
public class MovingAverageTrendCalculator implements Calculator {
    public static final int ROUNDING_MODE = BigDecimal.ROUND_CEILING;
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final BigDecimal smoothingLevels = new BigDecimal(4);
    private QuotesDao quotesDao;
    private int timeSeries = 1;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
    }

    //сглаживание ряда методом скользящих средних, 4 уровня сглаживания
    @Override
    public CalculatorResult calc(BaseQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        List<BaseQuote> statisticQuotes = quotesDao.getLastBaseQuotes(newQuote.getSymbol(), endPeriod.minusDays(timeSeries), endPeriod);
        statisticQuotes.add(newQuote);
        statisticQuotes.sort((o1, o2) -> {
            if (o1.getDatetime().isAfter(o2.getDatetime())) {
                return 1;
            } else if (o1.getDatetime().isBefore(o2.getDatetime())) {
                return -1;
            }
            return 0;
        });

        ArrayList<AverageQuote> smoothingAvgQuotes = getSmoothingAvgQuotes(getAvgQuotes(statisticQuotes));

        int sizeSmoothingQuotes = smoothingAvgQuotes.size();
        int sizeStatisticQuotes = statisticQuotes.size();
        if (sizeSmoothingQuotes < 3 || sizeStatisticQuotes < 3) {

            return new CalculatorResult(getBidForZeroVolume(newQuote), getOfferForZeroVolume(newQuote), 1, 1);
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

        return new CalculatorResult(forecastBid, forecastOffer, inaccuracyForBid, inaccuracyForOffer);
    }

    private BigDecimal calcForecast(BigDecimal averageQuote,
                                    BigDecimal valueForZeroVolume, BigDecimal valForZeroVolumePrev) {
        return averageQuote
                .add(valueForZeroVolume.subtract(valForZeroVolumePrev).divide(smoothingLevels, ROUNDING_MODE));
    }

    private BigDecimal getBidForZeroVolume(BaseQuote quote) {
        for (IPrice p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                return p.getBid();
            }
        }
        throw new FxException("Zero volume was not found");
    }

    private BigDecimal getOfferForZeroVolume(BaseQuote quote) {
        for (IPrice p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                return p.getOffer();
            }
        }
        throw new FxException("Zero volume was not found");
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
            statisticAvgQuotes.add(new AverageQuote(LocalDate.from(baseQuote.getDatetime()), baseQuote.getSymbol(),
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
            output.add(new AverageQuote(next.getDate(), next.getSymbol(),
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
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            BigDecimal factValue = getBidForZeroVolume(statisticQuotes.get(statisticQuotes.size() + i - 4 - 2));
            sumDeviations += smoothingAvgQuotes.get(i).getAverageQuoteBid().subtract(factValue)
                    .divide(factValue, ROUNDING_MODE).doubleValue() * 100;
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

    private double calcInaccuracyOffer(List<BaseQuote> statisticQuotes, ArrayList<AverageQuote> smoothingAvgQuotes) {
        double sumDeviations = 0.0;
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            BigDecimal factValue = getOfferForZeroVolume(statisticQuotes.get(statisticQuotes.size() + i - 4 - 2));
            sumDeviations += smoothingAvgQuotes.get(i).getAverageQuoteOffer().subtract(factValue)
                    .divide(factValue, ROUNDING_MODE).doubleValue() * 100;
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

}
