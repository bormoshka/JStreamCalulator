package ru.ulmc.bank.ui.common;

import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by User on 13.04.2017.
 */
public abstract class CrudWidget<T> extends VerticalLayout {
    private Grid<T> grid;
    private Button create;
    private Button delete;
    private Button edit;

    public CrudWidget() {
        initButtons();
        initGrid();
    }

    protected void initButtons() {
        create = new Button();
        addComponent(new HorizontalLayout());
    }

    protected void initGrid() {
        grid = new Grid<>();
        grid.setCaption("Double click to edit");
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        initColumns(grid);

        grid.getEditor().setEnabled(true);
    }

    protected abstract void initColumns(Grid<T> grid);


}
