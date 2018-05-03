package ru.ulmc.bank.ui.views.settings;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import org.vaadin.inputmask.InputMask;
import org.vaadin.inputmask.client.Alias;
import ru.ulmc.bank.calculators.util.CalculatorInfo;
import ru.ulmc.bank.calculators.util.CalculatorsLocator;
import ru.ulmc.bank.entities.configuration.SymbolCalculatorConfig;
import ru.ulmc.bank.ui.entity.CalculatorInfoData;
import ru.ulmc.bank.ui.entity.SymbolConfigModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CalculatorsPanel extends Panel {
    private Set<CalculatorInfo> infoList;
    private List<CalculatorInfoData> infoDataList = new ArrayList<>();
    private Grid<CalculatorInfoData> grid = new Grid<>();
    private DecimalFormat decFormat = new DecimalFormat("###.###");
    private TextField bidField;
    private TextField offerField;
    private Consumer<String> onChangeCallback;

    public CalculatorsPanel(Consumer<String> onChangeCallback) {
        this.onChangeCallback = onChangeCallback;
        init();
    }

    public void onSelect(SymbolConfigModel symbolConfig) {
        if (grid.getEditor().isOpen()) {
            grid.getEditor().cancel();
        }
        if (symbolConfig == null) {
            infoDataList.clear();
            return;
        }
        Map<String, SymbolCalculatorConfig> calculators = symbolConfig.getCalculators();

        infoDataList.clear();
        infoList.forEach(calculatorInfo -> {
            infoDataList.add(new CalculatorInfoData(symbolConfig.getSymbol(), calculatorInfo, calculators
                    .computeIfAbsent(calculatorInfo.getFullClassName(), s -> new SymbolCalculatorConfig(s, 0, 0))));
        });
        grid.getDataProvider().refreshAll();
    }

    private void init() {
        reinitField();
        infoList = CalculatorsLocator.collect();
        grid.setStyleGenerator(item -> (item.getBidModifierStr() == null || (item.getBidModifier() <= 0 && item.getOfferModifier() <= 0)) ? "gray-row" : "");
        grid.addColumn(CalculatorInfoData::getName)
                .setExpandRatio(11)
                .setCaption("Name");
        grid.addColumn(CalculatorInfoData::getBidModifierStr)
                .setCaption("Bid Modifier")
                .setWidth(80)
                .setStyleGenerator(calculatorInfoData -> "to-right")
                .setEditorComponent(bidField, CalculatorInfoData::setBidModifierStr);
        grid.addColumn(CalculatorInfoData::getOfferModifierStr)
                .setCaption("Offer Modifier")
                .setWidth(80)
                .setStyleGenerator(calculatorInfoData -> "to-right")
                .setEditorComponent(offerField, CalculatorInfoData::setOfferModifierStr);
        grid.addColumn(CalculatorInfoData::getClassName)
                .setWidth(200)
                .setCaption("Classname");
        grid.addColumn(CalculatorInfoData::getDescription)
                .setCaption("Description")
                .setExpandRatio(5)
                .setWidth(200)
                .setDescriptionGenerator(CalculatorInfoData::getDescription);

        grid.getEditor().setEnabled(true);
        grid.getEditor().setBuffered(true);
        grid.getEditor().addSaveListener(editorSaveEvent -> onChangeCallback.accept(editorSaveEvent.getBean().getSymbol()));
        grid.setSizeFull();
        setContent(grid);
        setSizeFull();
        grid.setSizeFull();
        grid.setDataProvider(DataProvider.ofCollection(infoDataList));
    }

    private void reinitField() {
        bidField = getTF();
        offerField = getTF();
    }

    private TextField getTF() {
        TextField tf = new TextField();
        InputMask mask = new InputMask(Alias.DECIMAL);
        mask.setDecimalProtect(true);
        mask.setMax("1.0");
        mask.setMin("0.0");
        mask.extend(tf);
        return tf;
    }
}
