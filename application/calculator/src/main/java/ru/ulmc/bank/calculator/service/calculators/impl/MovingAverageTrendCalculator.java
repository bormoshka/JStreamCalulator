package ru.ulmc.bank.calculator.service.calculators.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.service.calculators.Calculator;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Price;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class MovingAverageTrendCalculator implements Calculator {
    private final QuotesDao quotesDao;
    private int timeSeries = 1;
    private int smoothingLevels = 4;

    @Autowired
    public MovingAverageTrendCalculator(QuotesDao dao) {
        this.quotesDao = dao;
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
        double forecastBid = smoothingAvgQuotes.get(sizeSmoothingQuotes - 2).getAverageQuoteBid()
                + (getBidForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 1))
                - getBidForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 2))) / smoothingLevels;

        double forecastOffer = smoothingAvgQuotes.get(sizeSmoothingQuotes - 2).getAverageQuoteOffer()
                + (getOfferForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 1))
                - getOfferForZeroVolume(statisticQuotes.get(sizeStatisticQuotes - 2))) / smoothingLevels;

        double inaccuracyForBid = calcInaccuracyBid(statisticQuotes, smoothingAvgQuotes);
        double inaccuracyForOffer = calcInaccuracyOffer(statisticQuotes, smoothingAvgQuotes);

        return new CalculatorResult(forecastBid, forecastOffer, inaccuracyForBid, inaccuracyForOffer);
    }

    private double getBidForZeroVolume(BaseQuote quote) {
        Price newPrice = null;
        for (Price p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                newPrice = p;
            }
        }
        return newPrice.getBid().doubleValue();
    }

    private double getOfferForZeroVolume(BaseQuote quote) {
        Price newPrice = null;
        for (Price p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                newPrice = p;
                break;
            }
        }
        return newPrice.getOffer().doubleValue();
    }

    private List<AverageQuote> getAvgQuotes(List<BaseQuote> statisticQuotes) {
        List<AverageQuote> statisticAvgQuotes = new ArrayList<>();
        for (int i = 0; i < (statisticQuotes.size() - smoothingLevels + 1); i++) {
            double sumQuoteBid = 0.0;
            double sumQuoteOffer = 0.0;

            for (int j = 0; j < smoothingLevels; j++) {
                sumQuoteBid += getBidForZeroVolume(statisticQuotes.get(i + j));
                sumQuoteOffer += getOfferForZeroVolume(statisticQuotes.get(i + j));
            }

            BaseQuote baseQuote = statisticQuotes.get(i + smoothingLevels - 1);
            statisticAvgQuotes.add(new AverageQuote(baseQuote.getDatetime(), baseQuote.getSymbol(),
                    sumQuoteBid / smoothingLevels, sumQuoteOffer / smoothingLevels));
        }
        return statisticAvgQuotes;
    }

    private ArrayList<AverageQuote> getSmoothingAvgQuotes(List<AverageQuote> statisticAvgQuotes) {
        ArrayList<AverageQuote> output = new ArrayList<>();
        int i1 = statisticAvgQuotes.size() - 1;
        for (int i = 0; i < i1; i++) {
            AverageQuote current = statisticAvgQuotes.get(i);
            AverageQuote next = statisticAvgQuotes.get(i + 1);
            output.add(new AverageQuote(next.getDatetime(), next.getSymbol(),
                    (current.getAverageQuoteBid() + next.getAverageQuoteBid()) / 2,
                    (current.getAverageQuoteOffer() + next.getAverageQuoteOffer()) / 2));
        }
        return output;
    }

    private double calcInaccuracyBid(List<BaseQuote> statisticQuotes, ArrayList<AverageQuote> smoothingAvgQuotes) {
        double sumDeviations = 0.0;
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            double factValue = getBidForZeroVolume(statisticQuotes.get(statisticQuotes.size() + i - 4 - 2));
            sumDeviations += (smoothingAvgQuotes.get(i).getAverageQuoteBid() - factValue) / factValue * 100;
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

    private double calcInaccuracyOffer(List<BaseQuote> statisticQuotes, ArrayList<AverageQuote> smoothingAvgQuotes) {
        double sumDeviations = 0.0;
        for (int i = smoothingAvgQuotes.size() - 1; i > -1; i--) {
            double factValue = getOfferForZeroVolume(statisticQuotes.get(statisticQuotes.size() + i - 4 - 2));
            sumDeviations += (smoothingAvgQuotes.get(i).getAverageQuoteOffer() - factValue) / factValue * 100;
        }
        return sumDeviations / smoothingAvgQuotes.size();
    }

}
