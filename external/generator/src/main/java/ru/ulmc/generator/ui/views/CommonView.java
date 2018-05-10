package ru.ulmc.generator.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class CommonView extends AbstractFxmlView {

    public EventHandler<WindowEvent> getOnHideHandler() {
        return null;
    }

    public EventHandler<WindowEvent> getOnSnowHandler() {
        return null;
    }
}
