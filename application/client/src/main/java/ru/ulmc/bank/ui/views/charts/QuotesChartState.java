package ru.ulmc.bank.ui.views.charts;

import com.vaadin.shared.ui.JavaScriptComponentState;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

public class QuotesChartState extends JavaScriptComponentState {
    public BaseQuote baseQuote;
    public Quote calcQuote;
    public boolean redrawChart;
    public boolean destroy;
}