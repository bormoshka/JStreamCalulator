package ru.ulmc.bank.ui.views.charts;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.data.PointStyle;
import com.byteowls.vaadin.chartjs.options.FillMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.options.zoom.XYMode;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.bank.config.zookeeper.service.ConfigurationService;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
import ru.ulmc.bank.ui.views.CommonView;
import ru.ulmc.bank.ui.widgets.util.MenuSupport;

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoField.*;

@SpringView(name = QuotesView.NAME)
public class QuotesView extends CommonView implements View {
    static final String NAME = "quotesChart";
    public static final MenuSupport MENU_SUPPORT = new MenuSupport(NAME, "Просмотр котировок");
    private final QuotesDao dao;
    private final DateTimeField dateTimeFieldStart;
    private final DateTimeField dateTimeFieldEnd;
    private ConfigurationService service;
    private Predicate<BasePrice> basePricePredicate = basePrice -> basePrice.getVolume() == 0;
    private ChartJs chart;
    private boolean isInitialized = false;
    private DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yy hh:mm:ss")
            .toFormatter(Locale.getDefault());

    @Autowired
    public QuotesView(QuotesDao dao, ConfigurationService service) {

        this.dao = dao;
        this.service = service;

        setupRoot();
        ComboBox<String> symCombo = new ComboBox<>("Symbol");
        symCombo.setPlaceholder("Symbol");
        dateTimeFieldStart = new DateTimeField("Start date/time");
        dateTimeFieldStart.setValue(LocalDateTime.now().minusDays(1));
        dateTimeFieldEnd = new DateTimeField("End date/time");
        dateTimeFieldEnd.setValue(LocalDateTime.now());
        Button apply = new Button("Apply");
        apply.addClickListener(clickEvent -> {
            apply(dao, symCombo.getValue());
        });
        Collection<String> collect = service.getSymbols().stream().map(SymbolConfig::getSymbol).collect(Collectors.toList());
        symCombo.setItems(collect);
        symCombo.addValueChangeListener(valueChangeEvent -> {
            apply(dao, valueChangeEvent.getValue());
        });
        HorizontalLayout hl = new HorizontalLayout(symCombo, dateTimeFieldStart, dateTimeFieldEnd, apply);
        hl.setSpacing(true);

        chart = new ChartJs();
        layout.addComponents(hl, chart);
        initChart(Collections.emptyList(), Collections.emptyList());
        layout.setMargin(true);
    }

    private void apply(QuotesDao dao, String symbol) {
        LocalDateTime from = dateTimeFieldStart.getValue();
        LocalDateTime to = dateTimeFieldEnd.getValue();

        List<BaseQuote> lastBaseQuotes = dao.getLastBaseQuotes(symbol, from, to);
        List<Quote> lastCalcQuotes = dao.getLastCalcQuotes(symbol, from, to);
        initChart(lastBaseQuotes, lastCalcQuotes);
    }


    private void initChart(List<BaseQuote> baseQuotes, List<Quote> quotes) {
        List<String> labels = new ArrayList<>(baseQuotes.size());
        LineChartConfig config = new LineChartConfig();
        LineDataset baseBid = new LineDataset().fill(false);
        baseBid.label("BID");
        baseBid.pointStyle(PointStyle.circle);
        baseBid.borderColor("#1565C0");
        baseBid.backgroundColor("#1E88E5");
        LineDataset baseOffer = new LineDataset().fill(true, 1);
        baseOffer.borderColor("#1565C0");
        baseOffer.backgroundColor("rgba(33,150,243,0.5)");
        baseOffer.fill(FillMode.ORIGIN);
        baseOffer.label("OFFER");
        baseOffer.pointStyle(PointStyle.circle);

        baseQuotes.forEach(quote -> {
            quote.getPrices().stream().filter(basePricePredicate)
                    .findFirst().ifPresent(basePrice -> {
                String label = quote.getDatetime().format(formatter);
                baseBid.addData(basePrice.getBid().doubleValue());
                baseOffer.addData(basePrice.getOffer().doubleValue());
                labels.add(label);
            });
        });

        LineDataset calcBid = new LineDataset().fill(false);
        calcBid.label("CALC BID");
        calcBid.pointStyle(PointStyle.circle);
        calcBid.borderColor("rgba(255,87,34,1)");
        calcBid.backgroundColor("rgba(255,87,34,1)");
        LineDataset calcOffer = new LineDataset()
                .fill(true, 1)
                .fill(FillMode.ORIGIN);
        calcOffer.borderColor("rgba(255,87,34,1)");
        calcOffer.backgroundColor("rgba(255,87,34,0.4)");
        calcOffer.fill(FillMode.ORIGIN);
        calcOffer.label("CALC OFFER");
        calcOffer.pointStyle(PointStyle.circle);
        quotes.forEach(quote -> {
            quote.getPrices().stream().filter(calcPrice -> calcPrice.getVolume() == 0)
                    .findFirst().ifPresent(price -> {
                calcBid.addData(price.getBid().doubleValue());
                calcOffer.addData(price.getOffer().doubleValue());
            });
        });

        Double max = baseOffer.getData() != null ? baseOffer.getData().stream().mapToDouble(Double::doubleValue).max().orElse(0) : 0;
        Double min = baseBid.getData() != null ? baseBid.getData().stream().mapToDouble(Double::doubleValue).min().orElse(0) : 0;
        config
                .data()
                .labelsAsList(labels)
                .addDataset(baseOffer)
                .addDataset(baseBid)
                .addDataset(calcOffer)
                .addDataset(calcBid)
                .and();

        config.
                options()

                .zoom().mode(XYMode.XY).enabled(true).and()
                .responsive(true)
                .maintainAspectRatio(false)
                .elements()
                .line()
                .tension(0.00001d)
                .and()
                .and()
                .scales()
                .add(Axis.Y, new LinearScale()
                        .stacked(false).getThis()
                        .gridLines().display(true)
                        .and()
                        .ticks()
                        .beginAtZero(false)
                        //.maxTicksLimit(15)
                        .suggestedMax((int) (max + (max * 0.10)))
                        .suggestedMin((int) (min - (min * 0.10)))
                        .and())
                // .add(Axis.X, new LinearScale().stacked(false).gridLines().display(true).and())
                .and()
                .title()
                .display(true)
                .text("Advanced line fill options")
                .and()
                .done();

        //  for (int i = 0; i < config.data().getDatasets().size(); i++) {
        //      LineDataset lds = (LineDataset) config.data().getDatasetAtIndex(i);
        //      lds.label("D" + i);
        //      lds.borderColor(ColorUtils.toRgb(new int[]{100, 100, 100}));
        //      lds.backgroundColor(ColorUtils.toRgba(new int[]{200, 200, 200}, 0.5));
        //      // generate data
        //      lds.dataAsList(Arrays.asList(10d, 20d, 30d, 40d));
        //  }
        ChartJs chart = new ChartJs(config);
        //chart.configure(config);
        //chart.update();
        chart.setSizeFull();
        chart.setHeight("600px");
        chart.setJsLoggingEnabled(true);
        layout.replaceComponent(this.chart, chart);
        this.chart = chart;
    }
}
