package ru.ulmc.bank.ui.common;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by User on 03.04.2017.
 */
public class MessageWindow extends Window {
    private Label message;

    public MessageWindow(String caption) {
        super(caption);
        setWidth(400.0f, Unit.PIXELS);
        setModal(true);
        message = new Label();
        message.setContentMode(ContentMode.HTML);
        message.setResponsive(true);
        message.setSizeFull();
        message.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        VerticalLayout vl = new VerticalLayout(message);
        vl.setMargin(true);
        vl.setResponsive(true);
        setContent(vl);
    }

    public void setText(String text) {
        message.setValue(text);
    }
}
