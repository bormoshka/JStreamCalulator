package ru.ulmc.bank.ui.views;

import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorSaveListener;
import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.JsonValue;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonView extends CustomComponent {
    protected VerticalLayout layout;
    private Pattern simpleDoubleRegex = Pattern.compile("\\d+(\\.\\d{1,2})?");

    protected void setupRoot() {
        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(false);
        layout.setSizeFull();
        setCompositionRoot(layout);
    }


    protected ComboBox<Boolean> createBooleanComboBox() {
        ComboBox<Boolean> editor = new ComboBox<>("", Arrays.asList(true, false));
        editor.setEmptySelectionAllowed(false);
        editor.setItemCaptionGenerator(item -> item ? "Да" : "Нет");
        return editor;
    }

    protected Button createButton(String title, Button.ClickListener clickListener) {
        Button btn = new Button(title);
        btn.setEnabled(false);
        btn.setDisableOnClick(true);
        btn.addClickListener(clickListener);
        return btn;
    }

    protected <T> void defaultEditGridInit(Grid<T> grid, EditorSaveListener<T> save, EditorCancelListener<T> cancel) {
        defaultGridInit(grid, save, cancel, true);
    }

    protected <T> void defaultGridInit(Grid<T> grid, EditorSaveListener<T> save, EditorCancelListener<T> cancel, boolean isEdit) {
        grid.setSizeFull();
        grid.getEditor().setEnabled(isEdit);
        grid.getEditor().setSaveCaption("Сохранить");
        grid.getEditor().setCancelCaption("Отменить");
        if (save != null) {
            grid.getEditor().addSaveListener(save);
        }
        if (cancel != null) {
            grid.getEditor().addCancelListener(cancel);
        }
    }

    protected <T> Grid.Column<T, String> addColumn(Grid<T> grid,
                                                    ValueProvider<T, String> valueProvider,
                                                    String caption, int minWidth) {
        return grid.addColumn(valueProvider)
                .setCaption(caption)
                .setMinimumWidth(minWidth)
                .setExpandRatio(1);
    }

    protected <T> Grid.Column<T, Boolean> addColumnBool(Grid<T> grid,
                                                         ValueProvider<T, Boolean> valueProvider,
                                                         String caption, int minWidth) {
        return grid.addColumn(valueProvider)
                .setCaption(caption)
                .setRenderer(getBooleanTextRender())
                .setMinimumWidth(minWidth)
                .setExpandRatio(1);
    }

    protected <T> Grid.Column<T, Integer> addColumnInt(Grid<T> grid,
                                                        ValueProvider<T, Integer> valueProvider,
                                                        String caption, int minWidth) {
        return grid.addColumn(valueProvider)
                .setCaption(caption)
                .setMinimumWidth(minWidth)
                .setExpandRatio(1);
    }

    protected TextRenderer getBooleanTextRender() {
        return new TextRenderer("Нет") {
            @Override
            public JsonValue encode(Object value) {
                return super.encode(value == null || !Boolean.valueOf(value.toString()) ? "Нет" : "Да");
            }
        };
    }


    protected TextField getEditorWithDouble() {
        TextField editor = new TextField();
        editor.addValueChangeListener((HasValue.ValueChangeListener<String>) event -> {
            String val = event.getValue();
            Matcher simpleMatcher = simpleDoubleRegex.matcher(val);
            if (!simpleMatcher.matches()) {
                editor.addStyleName("error-grid-cell");
                editor.setDescription("Введены неверные данные"); //todo: change error description
            } else {
                editor.removeStyleName("error-grid-cell");
                editor.setDescription(null);
            }
        });
        return editor;
    }
}
