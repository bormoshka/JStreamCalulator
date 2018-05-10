package ru.ulmc.generator.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ulmc.generator.logic.beans.QuoteEntity;
import ru.ulmc.generator.logic.beans.Scenario;
import ru.ulmc.generator.logic.beans.ScenarioStep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@ToString
public class ScenarioProcess {
    private final double startBid;
    private final double startOffer;
    private String symbol;
    private double middle;
    private double bid;
    private double offer;
    private double interval;
    private Scenario scenario;
    private ThreadLocalRandom tlRand = ThreadLocalRandom.current();

    public ScenarioProcess(String symbol, double startBid, double startOffer, double interval, Scenario scenario) {
        this.symbol = symbol;
        this.startBid = startBid;
        this.startOffer = startOffer;
        this.bid = startBid;
        this.offer = startOffer;
        this.interval = interval;
        this.scenario = scenario;
    }

    public List<QuoteEntity> getQuotesToPublish() {
        List<QuoteEntity> quotes = new ArrayList<>();
        scenario.getSteps().forEach(step -> {
            middle = (bid + offer)/2;
            int i;
            int count = i = (int) ((step.getTimeUnits().toSeconds(step.getDuration()) / interval));
            double middleStep = getIncrement(step, middle, count);
            boolean randomTrendGoesUp = tlRand.nextBoolean();
            while (i-- > 0) {
                double v1 = 0.40 + tlRand.nextDouble();
                randomTrendGoesUp = tlRand.nextDouble() > v1 ? !randomTrendGoesUp : randomTrendGoesUp;
                double v = step.getVolatility() > 0 ? tlRand.nextDouble(step.getVolatility()) * (randomTrendGoesUp ? 1 : -1) : 0;
                double v2 = (tlRand.nextBoolean() ? 1 : -1 ) * tlRand.nextDouble(0.001);
                bid = bid + v * bid + v2 * bid + middleStep;
                offer = offer + v * offer + v2 * offer + middleStep;
                if (bid < 5 || offer < 5) {
                    v = tlRand.nextDouble();
                    bid = v * 3 + 5 ;
                    offer = v * 3 + 6;
                }
                if (bid >= offer) {
                    bid -= bid - offer * 2;
                }
                quotes.add(new QuoteEntity(symbol, BigDecimal.valueOf(bid), BigDecimal.valueOf(offer)));

            }
        });
        return quotes;
    }

    private double getIncrement(ScenarioStep step, double startValue, int count) {
        double pre = getPreIncrement(step, startValue, count);
        if (pre < 0) {
            while (startValue - count * pre <= 3) {
                pre = pre / 2;
            }
        }
        return pre;
    }

    private double getPreIncrement(ScenarioStep step, double startValue, int count) {
        double perPublishIncrement = startValue * step.getDelta() / count;
        switch (step.getTrend()) {
            case UP:
                return perPublishIncrement;
            case DOWN:
                return (-1) * perPublishIncrement;
            default:
            case HOLD:
                return 0;
        }
    }
}
