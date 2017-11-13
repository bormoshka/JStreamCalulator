package ru.ulmc.bank.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class AccessDeniedView extends VerticalLayout implements View {
    private Label message;

    private Button logout = new Button("Log out", this::logout);

    public AccessDeniedView() {
        setMargin(true);
        HorizontalLayout layout = new HorizontalLayout(message = new Label());
        layout.addComponent(logout);
        addComponent(layout);
        message.setSizeUndefined();
        message.addStyleName(ValoTheme.LABEL_FAILURE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        message.setValue(String.format("You do not have access to this view: %s", event.getViewName()));
    }

    public void logout(Button.ClickEvent e) {
        getUI().getPage().setLocation("logout");
    }
}
