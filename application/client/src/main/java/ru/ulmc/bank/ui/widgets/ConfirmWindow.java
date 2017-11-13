package ru.ulmc.bank.ui.widgets;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class ConfirmWindow extends CommonWindow {
    private String message;
    private Button.ClickListener dismissListener;
    private Button.ClickListener confirmListener;
    private Button noButton;
    private Button yesButton;

    public ConfirmWindow(String message) {
        this(message, null, null);
    }

    public ConfirmWindow(String message, Button.ClickListener dismissListener,
                         Button.ClickListener confirmListener) {
        super(540, 240);
        this.message = message;
        this.dismissListener = dismissListener;
        this.confirmListener = confirmListener;
        initComponent();
    }

    public void setConfirmListener(Button.ClickListener confirmListener) {
        this.confirmListener = confirmListener;
        yesButton.addClickListener(confirmListener);
    }

    public void setDismissListener(Button.ClickListener dismissListener) {
        this.dismissListener = dismissListener;
        noButton.addClickListener(dismissListener);
    }

    @Override
    protected Component createButtons() {
        noButton = new Button("Нет");
        if (dismissListener != null) {
            noButton.addClickListener(dismissListener);
        }
        noButton.setWidth("120px");
        noButton.setStyleName("small");

        yesButton = new Button("Да");
        yesButton.setWidth("120px");
        if (confirmListener != null) {
            yesButton.addClickListener(confirmListener);
        }
        yesButton.setStyleName("small");

        return new HorizontalLayout(yesButton, noButton);
    }

    @Override
    protected Component createMainComponent() {
        return createLabelComponent(message);
    }
}
