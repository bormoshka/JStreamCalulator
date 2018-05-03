package ru.ulmc.bank.ui.views.charts;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.util.Collections;
import java.util.List;

@JavaScript({
        "vaadin://lib/d3/d3.v4.min.js",
        "vaadin://lib/d3/d3-scale-chromatic.v0.3.min.js",
        "vaadin://lib/d3/d3-interpolate.v1.min.js",
        "vaadin://lib/d3/d3-selection.v1.min.js",
        "vaadin://lib/d3/d3-dispatch.v1.min.js",
        "vaadin://lib/d3/d3-timer.v1.min.js",
        "vaadin://lib/d3/d3-scale.v1.min.js",
        "vaadin://lib/d3/d3-color.v1.min.js",
        "vaadin://lib/d3/d3-ease.v1.min.js",
        "vaadin://lib/d3/d3-transition.v1.min.js",
        "vaadin://lib/polyfill/promise.min.js",
        "vaadin://lib/system/system-production.js",
        "vaadin://gen/chartsBundle.js",
        "vaadin://lib/requireChart.js",
})
public class QuotesChart extends AbstractJavaScriptComponent {

    private static final long serialVersionUID = 02L;

    public QuotesChart() {
        setSizeFull();
    }

    public void updateData(BaseQuote baseQuote, Quote calcQuote) {
        getState().baseQuote = baseQuote;
        getState().calcQuote = calcQuote;
        getState().redrawChart = false;
    }

    public void destroy() {
        getState().destroy = true;
    }

    public void setChartData(BaseQuote baseQuote, Quote calcQuote, Boolean redraw) {
        getState().baseQuote = baseQuote;
        getState().calcQuote = calcQuote;
        getState().redrawChart = redraw;
    }

    @Override
    protected QuotesChartState getState() {
        return (QuotesChartState) super.getState();
    }
}