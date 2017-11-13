package ru.ulmc.bank.ui.widgets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public class InfoWindow extends CommonWindow {
    private String message;

    public InfoWindow(String message) {
        super(540, 240);
        this.message = message;
        initComponent();
    }

    @Override
    protected Component createButtons() {
        Button btnOk = new Button("ОК");
        btnOk.setWidth("120px");
        btnOk.addClickListener(event -> close());
        btnOk.setVisible(true);
        btnOk.setStyleName("small");
        return btnOk;
    }

    @Override
    protected Component createMainComponent() {
        return createLabelComponent(message);
    }
}
