package ru.ulmc.bank.calculators.impl;

import lombok.NoArgsConstructor;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.inner.AverageQuote;
import ru.ulmc.bank.entities.inner.CalculatorResult;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Ordinary Least Squares
 */
@CalcPlugin(name = "Вычислитель МНК",
        description = "Математический метод, применяемый для решения различных задач, основанный на минимизации суммы" +
                " квадратов отклонений некоторых функций от искомых переменных")
@NoArgsConstructor
public class OlsTrendCalculator implements Calculator {
    private QuotesDao quotesDao;
    private int timeSeries = 90;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
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

        BigDecimal forecastBid = new BigDecimal(ratioAForBid * (statisticAvgQuotes.size() + 1) + ratioBForBid); //todo: расчеты в bigDecimal
        BigDecimal forecastOffer = BigDecimal.valueOf(ratioAForOffer * (statisticAvgQuotes.size() + 1) + ratioBForOffer);

        double inaccuracyForBid = calcInaccuracyBid(statisticAvgQuotes, ratioAForBid, ratioBForBid);
        double inaccuracyForOffer = calcInaccuracyOffer(statisticAvgQuotes, ratioAForOffer, ratioBForOffer);

        return new CalculatorResult(forecastBid, forecastOffer, inaccuracyForBid, inaccuracyForOffer);
    }

    private AverageQuote convertToAverageQuote(BaseQuote newQuote) {
        Price newPrice = null;
        for (Price p : newQuote.getPrices()) {
            if (p.getVolume() == 0) {
                newPrice = p;
            }
        }
        return new AverageQuote(newQuote.getDatetime(), newQuote.getSymbol(),
                newPrice.getBid(), newPrice.getOffer());
    }

    private double calcSumQuotesBid(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = 0.0;
        for (AverageQuote quote : averageQuotes) {
            sumQuotesBid += quote.getAverageQuoteBid().doubleValue();
        }
        return sumQuotesBid;
    }

    private double calcSumQuotesOffer(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = 0.0;
        for (AverageQuote quote : averageQuotes) {
            sumQuotesBid += quote.getAverageQuoteOffer().doubleValue();
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

    private BigDecimal calcSumMultiplyQuotesBidAndPeriod(ArrayList<AverageQuote> averageQuotes) {
        BigDecimal sumMultiplyQuotesAndPeriod = BigDecimal.ZERO;
        for (int i = 0; i < averageQuotes.size(); i++) {
            sumMultiplyQuotesAndPeriod = sumMultiplyQuotesAndPeriod.add(
                    averageQuotes.get(i).getAverageQuoteBid().multiply(BigDecimal.valueOf(i + 1)));
        }
        return sumMultiplyQuotesAndPeriod;
    }

    private BigDecimal calcSumMultiplyQuotesOfferAndPeriod(ArrayList<AverageQuote> averageQuotes) {
        BigDecimal sumMultiplyQuotesAndPeriod = BigDecimal.ZERO;
        for (int i = 0; i < averageQuotes.size(); i++) {
            sumMultiplyQuotesAndPeriod = sumMultiplyQuotesAndPeriod.add(
                    averageQuotes.get(i).getAverageQuoteOffer().multiply(BigDecimal.valueOf(i + 1)));
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
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesBidAndPeriod(averageQuotes).doubleValue(); // todo: fix
        double sumSqrtPeriod = calcSumSquarePeriod(averageQuotes);

        return ((sumMultiplyQuotesAndPeriod) - (sumQuotesBid * sumPeriods) / averageQuotes.size()) / (sumSqrtPeriod - sumPeriods * sumPeriods / averageQuotes.size());
    }

    private double calcRatioAForOffer(ArrayList<AverageQuote> averageQuotes) {
        double sumQuotesOffer = calcSumQuotesOffer(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesOfferAndPeriod(averageQuotes).doubleValue(); // todo: fix
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
            double factValue = averageQuotes.get(i).getAverageQuoteBid().doubleValue(); // todo: fix
            sumDeviations += ((ratioAForBid * (i + 1) + ratioBForBid) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }

    private double calcInaccuracyOffer(ArrayList<AverageQuote> averageQuotes, double ratioAForOffer, double ratioBForOffer) {
        double sumDeviations = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            double factValue = averageQuotes.get(i).getAverageQuoteOffer().doubleValue(); // todo: fix
            sumDeviations += ((ratioAForOffer * (i + 1) + ratioBForOffer) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }
}
