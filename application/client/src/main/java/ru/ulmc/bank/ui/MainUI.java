package ru.ulmc.bank.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.vaadin.spring.security.VaadinSecurity;
import org.vaadin.spring.security.util.SecurityExceptionUtils;
import ru.ulmc.bank.ui.views.ErrorView;
import ru.ulmc.bank.ui.views.HomeView;
import ru.ulmc.bank.ui.widgets.MainMenuBarBuilder;
import ru.ulmc.bank.ui.widgets.Notifier;
import ru.ulmc.bank.ui.views.LoginForm;

import javax.servlet.ServletException;

@SpringUI(path = "/*")
@Title("JBank. Интерфейс расчётного модуля")
@Theme("bank")
@VaadinSessionScope
@Push(transport = Transport.WEBSOCKET_XHR)
@Slf4j
public class MainUI extends UI {
    private final static Logger LOG = Logger.getLogger(MainUI.class.getName());

    @Autowired
    private VaadinSecurity vaadinSecurity;
    @Autowired
    private SpringViewProvider viewProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SpringNavigator navigator;
    @Value("${auth.logout.url}")
    private String logoutUrl;


    private MainMenuBarBuilder menu;
    private Panel content = new Panel();
    private VerticalLayout topLevelLayout = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(600);
        setSizeFull();

        initView();
        UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                if (SecurityExceptionUtils.isAccessDeniedException(event.getThrowable())) {
                    Notifier.humanized("Ошибка доступа");
                } else {
                    String cause = null;
                    for (Throwable t = event.getThrowable(); t != null; t = t.getCause()) {
                        if (t.getCause() == null) {
                            cause = t.getMessage();
                        }
                    }
                    Notifier.uncaughtException(event.getThrowable(),
                            (cause == null ? "Описание ошибки отсутствует." : cause));
                }
            }
        });
    }

    private void initView() {
        if (isUserAuthenticated()) {
            topLevelLayout.addStyleName("no-padding");
            topLevelLayout.setSpacing(true);
            topLevelLayout.setSizeFull();
            //content.addStyleName("content-padding");
            setContent(topLevelLayout);
            navigator.init(this, content);
            navigator.setErrorView(ErrorView.class);
            navigator.addProvider(viewProvider);
            initMenu();
            if (navigator.getState() == null || navigator.getState().isEmpty()
                    || !menu.haveView(navigator.getState())) {
                navigator.navigateTo(HomeView.NAME);
            } else {
                navigator.navigateTo(navigator.getState());
            }
            initContent();
            initVersion();
        } else {
            setContent(new LoginForm(authenticationManager, this::initView));
        }
    }

    private boolean isUserAuthenticated() {
        return isAuthEnabled()
                && vaadinSecurity.isAuthenticated()
                && !vaadinSecurity.isAuthenticatedAnonymously();
    }

    private boolean isAuthEnabled() {
        return vaadinSecurity.getAuthentication() != null;
    }

    private String getUserName() {
        String userName = "unknown";
        try {
            if (vaadinSecurity.getAuthentication() != null) {
                userName = vaadinSecurity.getAuthentication().getName();
            } else {
                log.warn("Аутентификация отключена");
            }
        } catch (Exception ex) {
            log.error("Не удалось получить имя пользователя", ex);
        }

        return userName;
    }

    private void initContent() {
        content.setSizeFull();
        content.setCaption(menu.getMenuSupport(navigator.getState()).getTitle());

        VerticalLayout vl = new VerticalLayout(content);
        vl.setSpacing(false);
        vl.setSizeFull();
        vl.setMargin(new MarginInfo(false, true));

        topLevelLayout.addComponent(vl);
        topLevelLayout.setExpandRatio(vl, 10);
    }

    private void initMenu() {
        menu = new MainMenuBarBuilder(navigator);
        AbstractLayout menuBar = menu.addReferenceGroup()
                .addOtherGroups()
                .addRightMenu(logoutUrl, getUserName(), this)
                .build();
        topLevelLayout.addComponent(menuBar);
        topLevelLayout.setExpandRatio(menuBar, 0);
    }

    private void initVersion() {
        Label label = new Label("<span class='version'>0.1</span>");
        label.setContentMode(ContentMode.HTML);
        topLevelLayout.addComponent(label);
        topLevelLayout.setExpandRatio(label, 0);
        topLevelLayout.setComponentAlignment(label, Alignment.BOTTOM_RIGHT);

    }

    @Component("vaadinServlet")
    public static class AppServlet extends SpringVaadinServlet {
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().setSystemMessagesProvider((SystemMessagesProvider) systemMessagesInfo -> {
                CustomizedSystemMessages messages = new CustomizedSystemMessages();
                // Don't show any messages, redirect immediately to the session expired URL
                messages.setSessionExpiredNotificationEnabled(false);
                // Don't show any message, reload the page instead
                messages.setCommunicationErrorNotificationEnabled(false);
                return messages;
            });
        }
    }
}
