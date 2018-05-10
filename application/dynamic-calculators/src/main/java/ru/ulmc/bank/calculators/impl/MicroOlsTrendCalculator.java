package ru.ulmc.bank.calculators.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculators.CalcSourceQuote;
import ru.ulmc.bank.calculators.util.CalcPlugin;
import ru.ulmc.bank.entities.inner.AverageQuote;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ordinary Least Squares
 */
@CalcPlugin(name = "Вычислитель МНК за микро промежутки",
        description = "Математический метод, применяемый для решения различных задач, основанный на минимизации суммы" +
                " квадратов отклонений некоторых функций от искомых переменных")
@NoArgsConstructor
@Slf4j
public class MicroOlsTrendCalculator extends OlsTrendCalculator {

    protected List<AverageQuote> getAverageQuotesForPeriod(CalcSourceQuote newQuote) {
        LocalDateTime endPeriod = LocalDateTime.now();
        return quotesDao.getLastAverageBaseQuotesOnZeroVolume(newQuote.getSymbol(), 5);
    }

}
