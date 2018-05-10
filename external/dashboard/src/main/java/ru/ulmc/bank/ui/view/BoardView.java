package ru.ulmc.bank.ui.view;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.NumberRenderer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import ru.ulmc.bank.bean.Currency;
import ru.ulmc.bank.bean.CurrencyRate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BoardView extends HorizontalLayout {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
    private Label mainLabel = new Label(getLabelText());

    public BoardView() {
        mainLabel.setStyleName("main-label");
        setSizeFull();
        ComponentContainer content = new CssLayout();
        content.addStyleName("view-content");
        content.setSizeFull();

        content.addComponent(new VerticalLayout(mainLabel, createGrid()));
        addComponent(content);
        setExpandRatio(content, 1.0f);
    }

    private Grid createGrid() {
        Grid<CurrencyRate> grid = new Grid<>();
        // grid.setSizeFull();
        grid.setWidth(100, Unit.PERCENTAGE);
        grid.addStyleName("big-grid");
        //grid.setRowHeight(40); //wtf?!
        grid.addColumn(CurrencyRate::getCurrencyCode)
                .setCaption("Валюта")
                .setStyleGenerator(budgetHistory -> "currency-row");
        grid.addColumn(CurrencyRate::getBid,
                new NumberRenderer(new DecimalFormat("#,##0.00")))
                .setCaption("Покупка")
                .setStyleGenerator(budgetHistory -> "currency-row");
        grid.addColumn(CurrencyRate::getAsk,
                new NumberRenderer(new DecimalFormat("#,##0.00")))
                .setCaption("Продажа")
                .setStyleGenerator(budgetHistory -> "currency-row");
        grid.getDefaultHeaderRow().setStyleName("currency-row");
        grid.setDataProvider(DataProvider.fromStream(getDataList()));
        //grid.getDataProvider().refreshAll();
        return grid;
    }

    private String getLabelText() {
        return "Актуальные котировки на " + sdf.format(new Date());
    }

    public static Stream<CurrencyRate> getDataList() {
        List<CurrencyRate> currRateList = new ArrayList<>();
        currRateList.add(getStubRate("USD", 30.2d, 33.13d));
        currRateList.add(getStubRate("EUR", 20.12d, 21.42d));
        currRateList.add(getStubRate("BIR", 11.3d, 13.87d));
        return currRateList.stream();
    }

    private static CurrencyRate getStubRate(String code, Double buy, Double sell) {
        return new CurrencyRate() {
            @Override
            public Currency getCurrency() {
                return null;
            }

            @Override
            public String getCurrencyCode() {
                return code;
            }

            @Override
            public Currency getBaseCurrency() {
                return null;
            }

            @Override
            public String getBaseCurrencyCode() {
                return null;
            }

            @Override
            public Double getBid() {
                return buy;
            }

            @Override
            public Double getAsk() {
                return sell;
            }
        };
    }
}