package ru.ulmc.bank.calculators.util;

import lombok.Getter;
import ru.ulmc.bank.bean.IPrice;
import ru.ulmc.bank.core.common.exception.FxException;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CalcUtils {
    static ThreadLocal<ThreadLocalParams> params = ThreadLocal.withInitial(ThreadLocalParams::new);

    public static BigDecimal calcModifiedBid(BigDecimal base, double modifier) {
        return base.subtract(base.multiply(BigDecimal.valueOf(modifier)));
    }

    public static BigDecimal calcModifiedOffer(BigDecimal base, double modifier) {
        return base.add(base.multiply(BigDecimal.valueOf(modifier)));
    }


    public static BigDecimal getBidForZeroVolume(BaseQuote quote) {
        for (IPrice p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                return p.getBid();
            }
        }
        throw new FxException("Zero volume was not found");
    }

    public static BigDecimal getOfferForZeroVolume(BaseQuote quote) {
        for (IPrice p : quote.getPrices()) {
            if (p.getVolume() == 0) {
                return p.getOffer();
            }
        }
        throw new FxException("Zero volume was not found");
    }

    public static BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }

    public static boolean isGreaterThan(BigDecimal is, BigDecimal than) {
        return is.compareTo(than) > 0;
    }

    public static boolean isSmallerThan(BigDecimal is, BigDecimal than) {
        return is.compareTo(than) < 0;
    }

    public static String f(Number n) {
        return params.get().nFormat.format(n);
    }

    public static class ThreadLocalParams {
        private NumberFormat nFormat = new DecimalFormat("0.0000000");

    }
}
