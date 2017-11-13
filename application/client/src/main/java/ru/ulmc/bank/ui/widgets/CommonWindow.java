package ru.ulmc.bank.ui.widgets;

import com.vaadin.ui.*;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public abstract class CommonWindow extends Window {
    protected VerticalLayout layout;

    public CommonWindow(int width, int height) {
        setResizable(false);
        setDraggable(false);
        setModal(true);
        center();
        setWidth(width, PIXELS);
        setHeight(height, PIXELS);
    }

    protected void initComponent() {
        Component mainComponent = createMainComponent();
        Component btns = createButtons();

        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.addComponent(mainComponent);
        layout.addComponent(btns);

        layout.setExpandRatio(mainComponent, 10);
        layout.setExpandRatio(btns, 0);
        layout.setComponentAlignment(btns, Alignment.MIDDLE_CENTER);

        setContent(layout);
    }

    protected Component createLabelComponent(String message) {
        Label label = new Label(message);
        label.setSizeFull();
        label.setStyleName("overflow-auto");
        label.setResponsive(true);
        return label;
    }

    protected abstract Component createButtons();
    protected abstract Component createMainComponent();
}
