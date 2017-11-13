package ru.ulmc.bank.ui.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SymbolUtil {

    public static String formatCoefficient(DecimalFormat decimalFormat, BigDecimal b, BigDecimal s) {
        return formatCoefficient(decimalFormat, b == null ? null : b.doubleValue(), s == null ? null : s.doubleValue());
    }

    public static String formatCoefficient(DecimalFormat decimalFormat, Double b, Double s) {
        String buy = decimalFormat.format(b == null ? s == null ? 0 : s : b);
        return (b != null && b.equals(s)) ? buy : buy + "/" + decimalFormat.format(s == null ? 0 : s);
    }
}
