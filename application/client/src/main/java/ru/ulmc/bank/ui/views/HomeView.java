package ru.ulmc.bank.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import ru.ulmc.bank.ui.widgets.util.MenuSupport;


@SpringView(name = HomeView.NAME)
public class HomeView extends VerticalLayout implements View {
    public static final String NAME = "home";
    public static final MenuSupport MENU_SUPPORT = new MenuSupport(NAME, "Стартовый экран");
    private Label message;

    public HomeView() {
        setMargin(true);

        ThemeResource resource = new ThemeResource("img/logo.png");
        Image logo = new Image();
        logo.setSource(resource);
        message = new Label();
        VerticalLayout layout = new VerticalLayout(logo, message);
        addComponent(layout);
        message.setSizeUndefined();
        message.addStyleName(ValoTheme.LABEL_H1);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        message.setValue("Стартовый экран");
    }

}
