package ru.ulmc.bank.ui.views;

import com.vaadin.data.HasValue;
import com.vaadin.ui.*;

public class ChangePasswordForm extends CustomComponent {
    private static final String FORM_CAPTION = "Change password";

    private Button changeButton = new Button("Change password");
    private Button cancelButton = new Button("Cancel");

    private PasswordField oldPwd = new PasswordField("Old password");
    private PasswordField newPwd = new PasswordField("New password");
    private PasswordField confirmPwd = new PasswordField("Confirm new password");

    private Window popup;

    public ChangePasswordForm() {
        if (getCompositionRoot() == null) {
            setCompositionRoot(createContent());
        }
    }

    public Window openInModalPopup() {
        setVisible(true);
        popup = new Window(FORM_CAPTION, this);
        popup.setModal(true);
        UI.getCurrent().addWindow(popup);
        return popup;
    }


    private Component createContent() {
        oldPwd.addValueChangeListener(this::fieldValueChange);
        newPwd.addValueChangeListener(this::fieldValueChange);
        confirmPwd.addValueChangeListener(this::fieldValueChange);

        changeButton.setEnabled(false);

        changeButton.addClickListener(this::changePwd);
        cancelButton.addClickListener(this::reset);
        HorizontalLayout layout = new HorizontalLayout(changeButton, cancelButton
        );
        FormLayout formLayout = new FormLayout(oldPwd, newPwd, confirmPwd);
        VerticalLayout verticalLayout = new VerticalLayout(formLayout, layout
        );
        verticalLayout.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
        return verticalLayout;
    }

    private void changePwd(final Button.ClickEvent event) {
        try {
            changePwdHandler.onChangePwd(
                    oldPwd.getValue(),
                    newPwd.getValue(),
                    confirmPwd.getValue()
            );
            reset(event);
        } catch (Exception e) {
            Notification.show("Error while changing password", e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void reset(final Button.ClickEvent event) {
        if (popup != null) {
            popup.close();
        }
    }

    private boolean isChangePwdEnabled(String oldPwdValue, String newPwdValue, String confirmPwdValue) {
        return !oldPwdValue.isEmpty() && !newPwdValue.isEmpty() && !confirmPwdValue.isEmpty()
                && !oldPwdValue.equals(newPwdValue) && newPwdValue.equals(confirmPwdValue);
    }

    private void fieldValueChange(final HasValue.ValueChangeEvent event) {
        changeButton.setEnabled(
                isChangePwdEnabled(
                        getCurText(oldPwd, event),
                        getCurText(newPwd, event),
                        getCurText(confirmPwd, event)
                ));
    }

    private String getCurText(PasswordField component, final HasValue.ValueChangeEvent event) {
        return event.getSource() == component ?
                event.getValue() != null ? event.getValue().toString() : ""
                : component.getValue();
    }

    public interface ChangePwdHandler {
        void onChangePwd(String oldPwd, String newPwd, String confirmedPwd);
    }

    private ChangePwdHandler changePwdHandler;

    public void setChangePwdHandler(final ChangePwdHandler changePwdHandler) {
        this.changePwdHandler = changePwdHandler;
    }
}
