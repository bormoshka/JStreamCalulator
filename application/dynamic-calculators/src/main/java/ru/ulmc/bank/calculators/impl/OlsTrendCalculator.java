package ru.ulmc.bank.calculators.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.bean.IPrice;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.ulmc.bank.calculators.util.CalcUtils.*;

/**
 * Ordinary Least Squares
 */
@CalcPlugin(name = "Вычислитель МНК",
        description = "Математический метод, применяемый для решения различных задач, основанный на минимизации суммы" +
                " квадратов отклонений некоторых функций от искомых переменных")
@NoArgsConstructor
@Slf4j
public class OlsTrendCalculator implements Calculator {
    protected QuotesDao quotesDao;
    private int timeSeries = 90;

    @Override
    public Calculator initialize(ResourcesEnvironment environment) {
        quotesDao = environment.getQuotesDao();
        return this;
    }

    //расчет прогнозной котировки методом наименьших квадратов
    @Override
    public CalculatorResult calc(CalcSourceQuote newQuote) {
        SymbolConfig symbolConfig = newQuote.getSymbolConfig();
        List<AverageQuote> statisticAvgQuotes = getAverageQuotesForPeriod(newQuote);
        BaseQuote quote = newQuote.getQuote();
        BigDecimal bidForZeroVolume = getBidForZeroVolume(quote);
        BigDecimal offerForZeroVolume = getOfferForZeroVolume(quote);
        AverageQuote incomingQuote = convertToAverageQuote(quote);
        statisticAvgQuotes.add(incomingQuote);

        double ratioAForBid = calcRatioAForBid(statisticAvgQuotes);
        double ratioBForBid = calcRatioBForBid(statisticAvgQuotes);
        double ratioAForOffer = calcRatioAForOffer(statisticAvgQuotes);
        double ratioBForOffer = calcRatioBForOffer(statisticAvgQuotes);
        try {
            BigDecimal forecastBid = new BigDecimal(ratioAForBid * (statisticAvgQuotes.size() + 1) + ratioBForBid); //todo: расчеты в bigDecimal
            BigDecimal forecastOffer = BigDecimal.valueOf(ratioAForOffer * (statisticAvgQuotes.size() + 1) + ratioBForOffer);
            double inaccuracyForBid = calcInaccuracyBid(statisticAvgQuotes, ratioAForBid, ratioBForBid);
            double inaccuracyForOffer = calcInaccuracyOffer(statisticAvgQuotes, ratioAForOffer, ratioBForOffer);

            BigDecimal minBaseBid = calcModifiedBid(bidForZeroVolume, symbolConfig.getBidBaseModifier());
            BigDecimal maxBaseBid = calcModifiedBid(bidForZeroVolume, symbolConfig.getBidMaxModifier());

            BigDecimal minBaseOffer = calcModifiedOffer(offerForZeroVolume, symbolConfig.getOfferBaseModifier());
            BigDecimal maxBaseOffer = calcModifiedOffer(offerForZeroVolume, symbolConfig.getOfferMaxModifier());

           // BigDecimal forecastBidWithInaccuracy = forecastBid.subtract(forecastBid.subtract(bidForZeroVolume).abs().multiply(bd(inaccuracyForBid)));
           // BigDecimal forecastOfferWithInaccuracy = forecastOffer.add(forecastOffer.subtract(offerForZeroVolume).abs().multiply(bd(inaccuracyForOffer)));
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
        } catch (Exception ex) {
            log.error("Calculation Failed", ex);
            return new CalculatorResult(calcModifiedBid(bidForZeroVolume, symbolConfig.getBidMaxModifier()),
                    calcModifiedOffer(offerForZeroVolume, symbolConfig.getOfferMaxModifier()));
        }
    }

    protected List<AverageQuote> getAverageQuotesForPeriod(CalcSourceQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        return quotesDao.getDailyAverageBaseQuotesOnZeroVolume(newQuote.getSymbol(), endPeriod.minusDays(timeSeries), endPeriod);
    }

    private AverageQuote convertToAverageQuote(BaseQuote newQuote) {
        IPrice newPrice = null;
        for (IPrice p : newQuote.getPrices()) {
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

    private BigDecimal calcSumMultiplyQuotesBidAndPeriod(List<AverageQuote> averageQuotes) {
        BigDecimal sumMultiplyQuotesAndPeriod = BigDecimal.ZERO;
        for (int i = 0; i < averageQuotes.size(); i++) {
            sumMultiplyQuotesAndPeriod = sumMultiplyQuotesAndPeriod.add(
                    averageQuotes.get(i).getAverageQuoteBid().multiply(BigDecimal.valueOf(i + 1)));
        }
        return sumMultiplyQuotesAndPeriod;
    }

    private BigDecimal calcSumMultiplyQuotesOfferAndPeriod(List<AverageQuote> averageQuotes) {
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

    private double calcRatioAForBid(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = calcSumQuotesBid(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesBidAndPeriod(averageQuotes).doubleValue(); // todo: fix
        double sumSqrtPeriod = calcSumSquarePeriod(averageQuotes);

        return ((sumMultiplyQuotesAndPeriod) - (sumQuotesBid * sumPeriods) / averageQuotes.size()) / (sumSqrtPeriod - sumPeriods * sumPeriods / averageQuotes.size());
    }

    private double calcRatioAForOffer(List<AverageQuote> averageQuotes) {
        double sumQuotesOffer = calcSumQuotesOffer(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);
        double sumMultiplyQuotesAndPeriod = calcSumMultiplyQuotesOfferAndPeriod(averageQuotes).doubleValue(); // todo: fix
        double sumSqrtQuotesOffer = calcSumSquarePeriod(averageQuotes);

        return ((sumMultiplyQuotesAndPeriod) - (sumQuotesOffer * sumPeriods) / averageQuotes.size()) / (sumSqrtQuotesOffer - sumPeriods * sumPeriods / averageQuotes.size());
    }

    private double calcRatioBForBid(List<AverageQuote> averageQuotes) {
        double sumQuotesBid = calcSumQuotesBid(averageQuotes);
        double ratioA = calcRatioAForBid(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);

        return sumQuotesBid / averageQuotes.size() - ratioA * sumPeriods / averageQuotes.size();
    }

    private double calcRatioBForOffer(List<AverageQuote> averageQuotes) {
        double sumQuotesOffer = calcSumQuotesOffer(averageQuotes);
        double ratioA = calcRatioAForOffer(averageQuotes);
        double sumPeriods = calcSumPeriod(averageQuotes);

        return sumQuotesOffer / averageQuotes.size() - ratioA * sumPeriods / averageQuotes.size();
    }

    private double calcInaccuracyBid(List<AverageQuote> averageQuotes, double ratioAForBid, double ratioBForBid) {
        double sumDeviations = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            double factValue = averageQuotes.get(i).getAverageQuoteBid().doubleValue(); // todo: fix
            sumDeviations += ((ratioAForBid * (i + 1) + ratioBForBid) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }

    private double calcInaccuracyOffer(List<AverageQuote> averageQuotes, double ratioAForOffer, double ratioBForOffer) {
        double sumDeviations = 0.0;
        for (int i = 0; i < averageQuotes.size(); i++) {
            double factValue = averageQuotes.get(i).getAverageQuoteOffer().doubleValue(); // todo: fix
            sumDeviations += ((ratioAForOffer * (i + 1) + ratioBForOffer) - factValue) / factValue * 100;
        }
        return sumDeviations / averageQuotes.size();
    }
}
