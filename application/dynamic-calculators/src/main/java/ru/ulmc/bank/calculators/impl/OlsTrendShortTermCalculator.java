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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.ulmc.bank.calculators.util.CalcUtils.*;

/**
 * Ordinary Least Squares
 */
@CalcPlugin(name = "Вычислитель МНК краткосрочный",
        description = "Математический метод, применяемый для решения различных задач, основанный на минимизации суммы" +
                " квадратов отклонений некоторых функций от искомых переменных")
@NoArgsConstructor
@Slf4j
public class OlsTrendShortTermCalculator extends OlsTrendCalculator {
    private int timeSeries = 30;

    protected List<AverageQuote> getAverageQuotesForPeriod(CalcSourceQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        return quotesDao.getMinutelyAverageBaseQuotesOnZeroVolume(newQuote.getSymbol(), endPeriod.minusMinutes(timeSeries), endPeriod);
    }
}
