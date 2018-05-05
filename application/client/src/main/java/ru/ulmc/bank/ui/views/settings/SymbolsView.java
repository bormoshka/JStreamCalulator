package ru.ulmc.bank.ui.views.settings;

import com.vaadin.data.*;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.EditorSaveEvent;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.bank.config.zookeeper.service.ConfigurationService;
import ru.ulmc.bank.core.common.Perms;
import ru.ulmc.bank.entities.configuration.Currency;
import ru.ulmc.bank.entities.configuration.SymbolConfig;
import ru.ulmc.bank.ui.entity.RowStatus;
import ru.ulmc.bank.ui.entity.SymbolConfigModel;
import ru.ulmc.bank.ui.views.CommonView;
import ru.ulmc.bank.ui.widgets.Notifier;
import ru.ulmc.bank.ui.widgets.util.MenuSupport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Представление справочника валютных пар
 */
@SpringView(name = SymbolsView.NAME)
public class SymbolsView extends CommonView implements View {
    static final String NAME = "fxSymbols";
    public static final MenuSupport MENU_SUPPORT = new MenuSupport(NAME, "Настройка валютных пар");
    private final Grid<SymbolConfigModel> grid = new Grid<>();
    private final transient ConfigurationService service;
    private boolean updatePermission;
    private boolean deletePermission;
    private boolean createPermission;
    private boolean readPermission;
    private Button btnDelete;
    private Button btnSave;
    private Button btnCancel;
    private ComboBox<String> iso1Editor;
    private ComboBox<String> iso2Editor;
    private TextField bidEditor;
    private TextField offerEditor;
    private DecimalFormat decFormat = new DecimalFormat("###.###");
    private Map<String, Grid.Column> columnsByName = new HashMap<>();
    private Button btnAdd;
    private Map<String, String> currencies;
    private transient List<SymbolConfigModel> allData = new ArrayList<>();
    private transient Set<SymbolConfigModel> selectedItems = new HashSet<>();
    private transient Set<SymbolConfigModel> preSavedEntities = new HashSet<>();
    private transient Set<String> preDeletedSymbols = new HashSet<>();
    private Binder<SymbolConfigModel> binder;
    private Runnable functionClearEditor;
    private long inGridIdCounter = 0;

    private CalculatorsPanel calcPanel;

    {
        decFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        decFormat.setParseBigDecimal(true);
    }

    @Autowired
    public SymbolsView(ConfigurationService service) {
        this.service = service;
        calcPanel = new CalculatorsPanel(s -> {
            allData.stream().filter(scm -> s.equalsIgnoreCase(scm.getSymbol())).findFirst()
                    .ifPresent(scm -> {
                        scm.setRowStatus(RowStatus.EDITED);
                      //  grid.getDataProvider().refreshItem(scm);
                    });
            toggleBtnStatus(btnSave, true);
        });
    }

    private void initComponent() {
        currencies = new HashMap<>();
        currencies = service.getCurrencies().stream()
                .collect(Collectors.toMap(Currency::getIso, Currency::getName));
        setSizeFull();
        setupRoot();
        if (hasCreatePermission()) {
            initEditors();
        }
        if (hasUpdatePermission()) {
            grid.getEditor().addSaveListener(this::onRowSave);
            grid.getEditor().setEnabled(true);
            grid.getEditor().setSaveCaption("ОК");
            grid.getEditor().setCancelCaption("Отменить");
        }
        initGrid();
        initControls();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        initPermissions();
        if (hasReadPermission()) {
            initComponent();
        } else {
            Notification.show("Недостаточно прав для просмотра данной страницы", Notification.Type.ERROR_MESSAGE);
        }
    }

    @Override
    protected void setupRoot() {
        super.setupRoot();
        layout.setSpacing(false);
        HorizontalSplitPanel c = new HorizontalSplitPanel(grid, calcPanel);
        c.setSizeFull();
        layout.addComponent(c);
        layout.setExpandRatio(c, 10);
    }

    private void initEditors() {
        iso1Editor = createIsoComboBox("");
        iso2Editor = createIsoComboBox("");
        bidEditor = createTextField();
        offerEditor = createTextField();

    }

    private void initGrid() {
        MultiSelectionModel<SymbolConfigModel> selectionModel
                = (MultiSelectionModel<SymbolConfigModel>) grid.setSelectionMode(Grid.SelectionMode.MULTI);
        selectionModel.addSelectionListener(this::toggleDeleteBtn);

        initHeader();
        if (hasUpdatePermission()) {
            initFooter();
        }

        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.getSelectionModel().addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(symbolConfigModel -> calcPanel.onSelect(symbolConfigModel)));
        grid.setStyleGenerator(this::chooseStyleNameForRow);
        grid.setDataProvider(DataProvider.ofCollection(allData));
        fetchData();
    }

    private <T> Grid.Column<SymbolConfigModel, T> createColumn(ValueProvider<SymbolConfigModel, T> valueProvider,
                                                               String caption) {
        return grid.addColumn(valueProvider)
                .setMinimumWidth(80)
                .setExpandRatio(1)
                .setCaption(caption);
    }

    private void initHeader() {
        Grid.Column<SymbolConfigModel, String> fxsColumn = storeColumn("base", createColumn(SymbolConfigModel::getBase, "ISO1"));
        Grid.Column<SymbolConfigModel, String> quotedCol = storeColumn("quoted", createColumn(SymbolConfigModel::getQuoted, "ISO2"));
        Grid.Column<SymbolConfigModel, String> bidBaseModifier = storeColumn("bidModifier", createColumn(SymbolConfigModel::getBidModifier, "Bid mod"));
        Grid.Column<SymbolConfigModel, String> offerBaseModifier = storeColumn("offerModifier", createColumn(SymbolConfigModel::getOfferModifier, "Offer mod"));

        grid.addComponentColumn(this::getToggleComponent)
                .setSortable(false)
                .setCaption("Вкл/Выкл")
                .setMinimumWidth(160)
                .setExpandRatio(0);
        if (hasUpdatePermission()) {
            fxsColumn.setEditorComponent(iso1Editor, SymbolConfigModel::setBase);
            quotedCol.setEditorComponent(iso2Editor, SymbolConfigModel::setQuoted);
            bidBaseModifier.setEditorComponent(bidEditor, SymbolConfigModel::setBidModifier);
            offerBaseModifier.setEditorComponent(offerEditor, SymbolConfigModel::setOfferModifier);
        }
    }

    private Component getToggleComponent(SymbolConfigModel ce) {
        Button toggle = new Button();
        toggle.setEnabled(hasUpdatePermission());
        toggle.setDisableOnClick(true);
        toggle.setWidth("150px");
        toggle.addClickListener(event -> {
            toggle.setEnabled(true);
            ce.setActive(!ce.getActive());
            service.changeActivation(ce.getSymbol(), ce.getActive());
            toggleStyle(ce.getActive(), toggle);
        });
        toggleStyle(ce.getActive(), toggle);
        return toggle;
    }

    private void toggleStyle(boolean isActive, Button toggle) {
        if (isActive) {
            toggle.setStyleName("toggle-on");
            toggle.setCaption("Включен");
        } else{
            toggle.setStyleName("toggle-off");
            toggle.setCaption("Выключен");
        }
        toggle.markAsDirty();
    }



    private boolean hasCreatePermission() {
        return createPermission;
    }

    private boolean hasAnyPermission(String... permissions) {
        return true;
    }

    private boolean hasUpdatePermission() {
        return updatePermission;
    }

    private boolean hasDeletePermission() {
        return deletePermission;
    }

    private boolean hasReadPermission() {
        return readPermission;
    }

    private void initPermissions() {
        updatePermission = true;
        deletePermission = true;
        createPermission = true;
        readPermission = true;
    }

    private void initFooter() {
        ComboBox<String> base = createIsoComboBox("Base currency");
        ComboBox<String> quoted = createIsoComboBox("Quoted currency");
        ComboBox<Boolean> yesNo = createBooleanComboBox();
        functionClearEditor = () -> {
            base.clear();
            quoted.clear();
            yesNo.clear();
            base.setComponentError(null);
            quoted.setComponentError(null);
            yesNo.setComponentError(null);
        };

        yesNo.setSelectedItem(true);
        base.setEmptySelectionAllowed(true);
        quoted.setEmptySelectionAllowed(true);
        String defWidth = "220px";
        base.setWidth(defWidth);
        quoted.setWidth(defWidth);
        yesNo.setWidth("80px");

        String reqStr = "Обязательное поле";
        binder = new Binder<>(SymbolConfigModel.class);

        binder.forField(base).withValidator((Validator<String>) (value, context) -> {
            if (value != null && value.equals(quoted.getValue())) {
                return ValidationResult.error("Базовая валюта должна отличаться от котируемой");
            }
            return ValidationResult.ok();
        }).asRequired(reqStr).bind("base");
        binder.forField(quoted).withValidator((Validator<String>) (value, context) -> {
            if (value != null && value.equals(base.getValue())) {
                return ValidationResult.error("Котируемая валюта должна отличаться от базовой");
            }
            return ValidationResult.ok();
        }).asRequired(reqStr).bind("quoted");
        binder.addValueChangeListener(event -> toggleBtnStatus(btnAdd, binder.isValid()));

        FooterRow footer = grid.prependFooterRow();
        footer.setStyleName("no-padding");

        footer.getCell(columnsByName.get("base")).setComponent(base);
        footer.getCell(columnsByName.get("quoted")).setComponent(quoted);
    }

    private <T> Grid.Column<SymbolConfigModel, T> storeColumn(String name, Grid.Column<SymbolConfigModel, T> column) {
        columnsByName.put(name, column);
        return column;
    }

    private String chooseStyleNameForRow(SymbolConfigModel ce) {
        return ce.getRowStatus().getStyle() + (ce.isValid() ? "" : "invalid-row ");
    }

    private void initControls() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(true);
        if (hasCreatePermission()) {
            btnAdd = createButton("Создать", this::onAdd);
        }
        if (hasDeletePermission()) {
            btnDelete = createButton("Удалить", this::onDelete);
        }
        if (hasAnyPermission(Perms.SYMBOL_GRID_CREATE, Perms.SYMBOL_GRID_UPDATE,
                Perms.SYMBOL_GRID_DELETE)) {
            btnCancel = createButton("Отменить", this::onCancel);
            btnSave = createButton("Применить", this::onSave);
        }
        addIfNotNull(hl, btnSave);
        addIfNotNull(hl, btnDelete);
        addIfNotNull(hl, btnAdd);
        addIfNotNull(hl, btnCancel);
        layout.addComponent(hl);
        layout.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(hl, 0);
    }

    private void addIfNotNull(HorizontalLayout hl, Button btn) {
        if (btn != null) {
            hl.addComponent(btn);
        }
    }

    private ComboBox<String> createIsoComboBox(String caption) {
        ComboBox<String> editor = new ComboBox<>("", currencies.keySet());
        editor.setItemCaptionGenerator(item -> item + " (" + currencies.get(item) + ")");
        editor.setScrollToSelectedItem(true);
        editor.setTextInputAllowed(true);
        editor.setPlaceholder(caption);
        editor.setEmptySelectionAllowed(false);
        editor.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        editor.setPopupWidth("400px");
        return editor;
    }

    private TextField createTextField() {
        TextField editor = new TextField();
        editor.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        return editor;
    }

    private void onSave(Button.ClickEvent event) {
        List<SymbolConfigModel> rowsWithErrors = preSavedEntities.stream()
                .filter(it -> !it.isValid())
                .collect(Collectors.toList());
        if (rowsWithErrors.isEmpty()) {
            try {
                removePhantomPairs();
                preDeletedSymbols.forEach(service::deleteSymbol);
                Set<SymbolConfig> currentSymbols = allData.stream().filter(scm -> !preDeletedSymbols.contains(scm.getSymbol()))
                        .map(SymbolConfigModel::getSymbolEntity)
                        .collect(Collectors.toSet());
                service.saveSymbols(currentSymbols);

                preDeletedSymbols.clear();
                preSavedEntities.clear();
                resetState();
                Notification.show("Операция выполнена успешно!",
                        "Изменения сохранены успешно", Notification.Type.TRAY_NOTIFICATION);
                fetchData();
            } catch (Throwable ex) {
                Notifier.logAndReportError(ex, "Ошибка выполнения операции! " + ex.getMessage());
            }
        } else {
            StringBuilder sb = new StringBuilder("Неккоректные пары: ");
            rowsWithErrors.forEach(ce -> sb
                    .append(ce.getBase()).append("/").append(ce.getQuoted()).append(" "));
            Notification.show("Ошибка: ", sb.toString(), Notification.Type.ERROR_MESSAGE);
            toggleBtnStatus(btnSave, true);
        }
    }

    private void removePhantomPairs() {
        Set<SymbolConfigModel> temporarySet = new HashSet<>(preSavedEntities);
        preSavedEntities.removeAll(preSavedEntities.stream()
                .filter(symbolConfigModel -> preDeletedSymbols.contains(symbolConfigModel.getSymbol()))
                .collect(Collectors.toSet()));
        preDeletedSymbols.removeAll(temporarySet.stream().map(SymbolConfigModel::getSymbol)
                .collect(Collectors.toSet()));
    }

    private void onDelete(Button.ClickEvent event) {
        preDeletedSymbols.addAll(selectedItems.stream()
                .map(SymbolConfigModel::getSymbol).collect(Collectors.toList()));
        selectedItems.forEach(symbolConfigModel -> symbolConfigModel.setRowStatus(RowStatus.MARKED_FOR_DELETION));
        grid.asMultiSelect().clear();
        toggleBtnStatus(btnSave, true);
        toggleBtnStatus(btnCancel, true);
    }

    private void onCancel(Button.ClickEvent event) {
        fetchData();
        preSavedEntities.clear();
        resetState();
    }

    private void onAdd(Button.ClickEvent event) {
        try {
            if (binder.isValid()) {
                SymbolConfigModel bean = new SymbolConfigModel(iso1Editor.getValue() + "/" + iso2Editor.getValue());
                binder.writeBean(bean);
                SymbolConfigModel ce = validateBean(processBean(bean));
                if (isSymbolAlreadyExists(ce.getSymbol())) {
                    Notification.show("Запись уже существует", ce.getSymbol(), Notification.Type.HUMANIZED_MESSAGE);
                    return;
                }
                ce.setRowStatus(RowStatus.CREATED);
                addNewItem(ce);
                Notification.show(ce.getSymbol(), "Запись добавлена. " +
                                "Для сохранения изменений нажмите кнопку \"Применить\"",
                        Notification.Type.TRAY_NOTIFICATION);
                if (btnSave != null && !btnSave.isEnabled()) {
                    toggleBtnStatus(btnSave, true);
                    toggleBtnStatus(btnCancel, true);
                }
            } else {
                Notification.show("Ошибка:", "Некорректные данные", Notification.Type.ERROR_MESSAGE);
            }
        } catch (ValidationException e) {
            Notification.show("Ошибка:", "Некорректные данные", Notification.Type.ERROR_MESSAGE);
        }
    }

    private boolean isSymbolAlreadyExists(String symbol) {
        return allData.stream().anyMatch(symbolConfigModel -> symbolConfigModel.getSymbol().equals(symbol));
    }

    private void addNewItem(SymbolConfigModel entity) {
        allData.add(entity);
        preSavedEntities.add(entity);
        // grid.getDataProvider().refreshItem(entity);
        grid.getDataProvider().refreshAll();
    }

    private void fetchData() {
        allData.clear();
        allData.addAll(service.getSymbols()
                .stream().map(this::wrap).collect(Collectors.toSet()));
        grid.getDataProvider().refreshAll();
    }

    private void resetState() {
        fetchData();
        selectedItems.clear();
        grid.getSelectionModel().deselectAll();
        if (hasUpdatePermission()) {
            functionClearEditor.run();
        }
        toggleBtnStatus(btnAdd, false);
        toggleBtnStatus(btnSave, false);
        toggleBtnStatus(btnDelete, false);
        toggleBtnStatus(btnCancel, false);
    }

    private void toggleDeleteBtn(SelectionEvent<SymbolConfigModel> event) {
        selectedItems.clear();
        selectedItems.addAll(event.getAllSelectedItems());
        toggleBtnStatus(btnDelete, !selectedItems.isEmpty());
    }


    private void onRowSave(EditorSaveEvent<SymbolConfigModel> event) {
        if (!hasUpdatePermission()) {
            return;
        }

        if (btnSave != null && !btnSave.isEnabled()) {
            toggleBtnStatus(btnSave, true);
            toggleBtnStatus(btnCancel, true);
        }

        SymbolConfigModel bean = event.getBean();
        validateBean(bean);
        processBean(bean);
        // bean.setRowStatus(event.getSource().getBinder().hasChanges() ? RowStatus.EDITED : RowStatus.NOT_CHANGED);
        if (bean.getRowStatus().equals(RowStatus.MARKED_FOR_DELETION)) {
            preDeletedSymbols.remove(bean); //снять пометку на удаление
        }
        bean.setRowStatus(RowStatus.EDITED);
        preSavedEntities.add(bean);
        grid.getDataProvider().refreshItem(bean);
    }

    private SymbolConfigModel wrap(SymbolConfig bean) {
        SymbolConfigModel symbolConfigModel = new SymbolConfigModel(bean);

        return validateBean(symbolConfigModel);
    }

    private SymbolConfigModel validateBean(SymbolConfigModel bean) {
        boolean isValid = bean.getQuoted() != null && bean.getBase() != null;
        isValid = isValid && !bean.getQuoted().equals(bean.getBase());
        bean.setValid(isValid);
        return bean;
    }

    private SymbolConfigModel processBean(SymbolConfigModel bean) {
        bean.setSymbol(bean.getBase() + "/" + bean.getQuoted());
        return bean;
    }

    private void toggleBtnStatus(Button btn, boolean status) {
        if (btn != null) {
            btn.setEnabled(status);
        }
    }

}
