package ru.ulmc.bank.ui.views.monitoring;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.HeaderRow;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.bank.config.zookeeper.service.ConfigurationService;
import ru.ulmc.bank.dao.QuotesDao;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.entities.inner.ActualQuotes;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;
import ru.ulmc.bank.ui.entity.SymbolsQuotesModel;
import ru.ulmc.bank.ui.views.CommonView;
import ru.ulmc.bank.ui.widgets.util.MenuSupport;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringView(name = QuotesMonitoringView.NAME)
public class QuotesMonitoringView extends CommonView implements View {
    static final String NAME = "quotesChart";
    public static final MenuSupport MENU_SUPPORT = new MenuSupport(NAME, "Просмотр актуальных котировок");
    private final QuotesDao dao;
    private final TreeGrid<SymbolsQuotesModel> grid = new TreeGrid<>();
    private ConfigurationService service;
    private transient Map<String, List<SymbolsQuotesModel>> allData = new HashMap<>();

    private DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yy HH:mm:ss")
            .toFormatter(Locale.getDefault());

    private DecimalFormat decFormat = new DecimalFormat("###.####");

    @Autowired
    public QuotesMonitoringView(QuotesDao dao, ConfigurationService service) {

        this.dao = dao;
        this.service = service;
        setupRoot();
        layout.addComponent(grid);
        initGrid();
        setSizeFull();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        initView();
    }

    private void initView() {
        List<String> symbols = service.getSymbols().stream().map(SymbolConfig::getSymbol).collect(Collectors.toList());
        List<ActualQuotes> lastBaseQuotes = dao.getLastBaseQuotes(symbols);
        allData = lastBaseQuotes.stream().map(actualQuotes -> {
            BaseQuote baseQuote = actualQuotes.getBaseQuote();
            Quote calcQuote = actualQuotes.getCalcQuote();

            if (baseQuote != null) {
                SymbolsQuotesModel group = new SymbolsQuotesModel();
                group.setSymbol(actualQuotes.getSymbol());
                return baseQuote.getPrices().stream().map(basePrice -> {
                    SymbolsQuotesModel sqm = new SymbolsQuotesModel();
                    sqm.setVolume(String.valueOf(basePrice.getVolume()));
                    sqm.setSymbol(actualQuotes.getSymbol());
                    if (calcQuote != null) {
                        calcQuote.getPrices().stream()
                                .filter(calcPrice -> calcPrice.getVolume() == basePrice.getVolume())
                                .findFirst().ifPresent(cp -> {
                            sqm.setCalcBid(decFormat.format(cp.getBid()));
                            sqm.setCalcOffer(decFormat.format(cp.getOffer()));
                            sqm.setCalcDate(formatter.format(calcQuote.getDatetime()));
                        });
                    }
                    sqm.setBaseBid(decFormat.format(basePrice.getBid()));
                    sqm.setBaseOffer(decFormat.format(basePrice.getOffer()));
                    sqm.setBaseDate(formatter.format(baseQuote.getDatetime()));
                    return  sqm;
                }).collect(Collectors.toList());
            } else {
                SymbolsQuotesModel sqm = new SymbolsQuotesModel();
                sqm.setSymbol(actualQuotes.getSymbol());
                return Collections.singletonList(sqm);
            }
        }).collect(Collectors.toMap(lst -> lst.get(0).getSymbol(), o -> o));
        List<SymbolsQuotesModel> rootItems = allData.keySet().stream().map(SymbolsQuotesModel::new).collect(Collectors.toList());
        grid.setItems(rootItems, symbolsQuotesModel -> symbolsQuotesModel.isParent() ? allData.get(symbolsQuotesModel.getSymbol()) : Collections.emptyList());

    }

    private void initGrid() {
        HeaderRow header = grid.prependHeaderRow();
        grid.setHierarchyColumn(grid.addColumn(SymbolsQuotesModel::getSymbol).setCaption("Валютная пара"));
        grid.addColumn(SymbolsQuotesModel::getVolume).setCaption("Объем");

        header.join(
                grid.addColumn(SymbolsQuotesModel::getBaseDate).setCaption("Дата"),
                grid.addColumn(SymbolsQuotesModel::getBaseBid).setCaption("Bid"),
                grid.addColumn(SymbolsQuotesModel::getBaseOffer).setCaption("Offer"))
                .setText("Базовая котировка");

        header.join(
                grid.addColumn(SymbolsQuotesModel::getCalcDate).setCaption("Дата"),
                grid.addColumn(SymbolsQuotesModel::getCalcBid).setCaption("Bid"),
                grid.addColumn(SymbolsQuotesModel::getCalcOffer).setCaption("Bid"))
                .setText("Расчетная котировка");
        grid.setSizeFull();

    }
}
