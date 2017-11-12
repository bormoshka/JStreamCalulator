package ru.ulmc.bank.calculator.service.calculators.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ulmc.bank.calculator.dao.QuotesDao;
import ru.ulmc.bank.calculator.entity.*;
import ru.ulmc.bank.calculator.service.calculators.Calculator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MnkTrendCalculator implements Calculator {
    private final QuotesDao quotesDao;
    private int timeSeries = 90;

    @Autowired
    public MnkTrendCalculator(QuotesDao dao) {
        this.quotesDao = dao;
    }

    //расчет прогнозной котировки методом наименьших квадратов
    @Override
    public CalculatorResult calc(BaseQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        ArrayList<AverageQuote> statisticAvgQuotes = quotesDao.getDailyAverageBaseQuotes(newQuote.getSymbol(), endPeriod.minusDays(timeSeries), endPeriod);
        statisticAvgQuotes.add(convertToAverageQuote(newQuote));

        double ratioAForBid = calcRatioAForBid(statisticAvgQuotes);
        double ratioBForBid = calcRatioBForBid(statisticAvgQuotes);
        double ratioAForOffer = calcRatioAForOffer(statisticAvgQuotes);
        double ratioBForOffer = calcRatioBForOffer(statisticAvgQuotes);

        double forecastBid = ratioAForBid * (statisticAvgQuotes.size() + 1) + ratioBForBid;
        double forecastOffer = ratioAForOffer * (statisticAvgQuotes.size() + 1) + ratioBForOffer;

        double inaccuracyForBid = calcInaccuracyBid(statisticAvgQuotes, ratioAForBid, ratioBForBid);
        double inaccuracyForOffer = calcInaccuracyOffer(statisticAvgQuotes, ratioAForOffer, ratioBForOffer);

        return new CalculatorResult(forecastBid, forecastOffer, inaccuracyForBid, inaccuracyForOffer);
    }

    private AverageQuote convertToAverageQuote(BaseQuote newQuote) {
        Price newPrice = null;
        for (Price p : newQuote.getPrices()) {
            if (p.getVolume().equals(Volume.zeroVolume)) {
                newPrice = p;
            }
        }
        return new AverageQuote(newQuote.getDatetime(), newQuote.getSymbol(),
                newPrice.getBid().doubleValue(), newPrice.getOffer().doubleValue());
    }

    private double calcSumQuotesBid(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = 0.0;
        for (AverageQuote quote : averageQuotes) {
            sumQuotesBid += quote.getAverageQuoteBid();
        }
        return sumQuotesBid;
    }

    private double calcSumQuotesOffer(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = 0.0;
        for (AverageQuote quote : averageQuotes) {
            sumQuotesBid += quote.getAverageQuoteOffer();
        }
        return sumQuotesBid;
    }

    private double calcSumPeriod(List<AverageQuote> averageQuotes) {
        double sumPeriods = 0.0;
        for (int i = 1; i <= averageQuotes.size(); i++) {
            sumPeriods += i;
        }
        return sumPeriods;
    }

    private double calcSumMultiplyQuotesBidAndPeriod(ArrayList<AverageQuote> averageQuotes) {
        double sumMultiplyQuotesAndPeriod = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            sumMultiplyQuotesAndPeriod += averageQuotes.get(i).getAverageQuoteBid() * (i + 1);
        }
        return sumMultiplyQuotesAndPeriod;
    }

    private double calcSumMultiplyQuotesOfferAndPeriod(ArrayList<AverageQuote> averageQuotes) {
        double sumMultiplyQuotesAndPeriod = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            sumMultiplyQuotesAndPeriod += averageQuotes.get(i).getAverageQuoteOffer() * (i + 1);
        }
        return sumMultiplyQuotesAndPeriod;
    }

    private double calcSumSquarePeriod(List<AverageQuote> averageQuotes) {
        double sumSquarePeriod = 0.0;
        for (int i = 1; i <= averageQuotes.size(); i++) {
            sumSquarePeriod += i * i;
        }
        return sumSquarePeriod;
    }

    private double calcRatioAForBid(ArrayList<AverageQuote> averageQuotes) {
        double sumQuotesBid = calcSumQuotesBid(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesBidAndPeriod(averageQuotes);
        double sumSqrtPeriod = calcSumSquarePeriod(averageQuotes);

        return ((sumMultiplyQuotesAndPeriod) - (sumQuotesBid * sumPeriods) / averageQuotes.size()) / (sumSqrtPeriod - sumPeriods * sumPeriods / averageQuotes.size());
    }

    private double calcRatioAForOffer(ArrayList<AverageQuote> averageQuotes) {
        double sumQuotesOffer = calcSumQuotesOffer(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesOfferAndPeriod(averageQuotes);
        double sumSqrtQuotesOffer = calcSumSquarePeriod(averageQuotes);

        return ((sumMultiplyQuotesAndPeriod) - (sumQuotesOffer * sumPeriods) / averageQuotes.size()) / (sumSqrtQuotesOffer - sumPeriods * sumPeriods / averageQuotes.size());
    }

    private double calcRatioBForBid(ArrayList<AverageQuote> averageQuotes) {
        double sumQuotesBid = calcSumQuotesBid(averageQuotes);
        double ratioA = calcRatioAForBid(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);

        return sumQuotesBid / averageQuotes.size() - ratioA * sumPeriods / averageQuotes.size();
    }

    private double calcRatioBForOffer(ArrayList<AverageQuote> averageQuotes) {
        double sumQuotesOffer = calcSumQuotesOffer(averageQuotes);
        double ratioA = calcRatioAForOffer(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);

        return sumQuotesOffer / averageQuotes.size() - ratioA * sumPeriods / averageQuotes.size();
    }

    private double calcInaccuracyBid(ArrayList<AverageQuote> averageQuotes, double ratioAForBid, double ratioBForBid) {
        double sumDeviations = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            double factValue = averageQuotes.get(i).getAverageQuoteBid();
            sumDeviations += ((ratioAForBid * (i + 1) + ratioBForBid) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }

    private double calcInaccuracyOffer(ArrayList<AverageQuote> averageQuotes, double ratioAForOffer, double ratioBForOffer) {
        double sumDeviations = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            double factValue = averageQuotes.get(i).getAverageQuoteOffer();
            sumDeviations += ((ratioAForOffer * (i + 1) + ratioBForOffer) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }
}
