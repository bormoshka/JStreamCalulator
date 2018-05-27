package ru.ulmc.generator.logic;

import ru.ulmc.generator.logic.beans.QuoteEntity;

import java.util.List;

public interface QuotesSource {
    List<QuoteEntity> getQuotesToPublish();
    String getSymbol();
    double getInterval();
}
