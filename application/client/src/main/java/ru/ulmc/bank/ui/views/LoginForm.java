package ru.ulmc.bank.ui.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoginForm extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginForm.class);

    private final AuthenticationManager authenticationManager;
    private Runnable onSuccess;

    private Button signIn;
    private TextField username;
    private PasswordField password;
    private Label msgLabel;

    public LoginForm(AuthenticationManager vaadinSecurity, Runnable onSuccess) {
        this.authenticationManager = vaadinSecurity;
        this.onSuccess = onSuccess;
        setSizeFull();
        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
    }

    private Component buildLoginForm() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.addStyleName("userName-panel");
        vl.addComponent(buildFields());

        Panel panel = new Panel(vl);
        panel.setWidth("500px");
        panel.setCaption("[АС АДиУК] Модуль настройки и мониторинга");
        Responsive.makeResponsive(panel);
        return panel;
    }

    private Component buildFields() {
        VerticalLayout fields = new VerticalLayout();
        fields.setSpacing(true);
        fields.addStyleName("no-top-padding");
        fields.addStyleName("fields");

       /* Label title = new Label("Fx Pricing. Расчётный модуль");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_LIGHT);*/

        msgLabel = new Label("");
        msgLabel.setSizeUndefined();
        msgLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        msgLabel.setVisible(false);

        username = new TextField("Логин");
        username.setIcon(VaadinIcons.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        username.setWidth("100%");
        username.focus();

        password = new PasswordField("Пароль");
        password.setIcon(VaadinIcons.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        password.setWidth("100%");

        signIn = new Button("Войти");
        signIn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signIn.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signIn.setWidth("100%");
        signIn.setDisableOnClick(true);
        fields.addComponents(msgLabel, username, password, signIn);

        signIn.addClickListener((Button.ClickListener) event -> login());
        return fields;
    }

    private void login() {
        try {
            signIn.setEnabled(false);

            Authentication token = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username.getValue(), password.getValue()));
            // Reinitialize the session to protect against session fixation attacks. This does not work
            // with websocket communication.
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            SecurityContextHolder.getContext().setAuthentication(token);
            onSuccess.run();
        } catch (AuthenticationException ex) {
            username.focus();
            username.selectAll();
            password.setValue("");
            msgLabel.setValue("Ошибка авторизации!");

            msgLabel.addStyleName(ValoTheme.LABEL_FAILURE);
            msgLabel.setVisible(true);
            LOGGER.error("Unexpected error while logging in", ex);
        } catch (Exception ex) {
            Notification.show("An unexpected error occurred", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            LOGGER.error("Unexpected error while logging in", ex);
        } finally {
            signIn.setEnabled(true);
        }
    }

}