package ru.ulmc.generator.ui.views;

import de.felixroske.jfxsupport.FXMLView;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.generator.logic.beans.Scenario;
import ru.ulmc.generator.ui.ScenarioEditorController;
import ru.ulmc.generator.ui.ScenariosSelectController;

@FXMLView
public class ScenarioSelectView extends CommonView {
    public static Scenario currentScenario;
    private final ScenariosSelectController scenariosSelectController;

    @Autowired
    public ScenarioSelectView(ScenariosSelectController scenariosSelectController,
                              ScenarioEditorController scenarioEditorController) {
        super();
        this.scenariosSelectController = scenariosSelectController;
    }

    @Override
    public EventHandler<WindowEvent> getOnHideHandler() {
        Window window = getView().getScene().getWindow();
        return event ->  scenariosSelectController.onClose(window);
    }

    @Override
    public EventHandler<WindowEvent> getOnSnowHandler() {
        return event -> scenariosSelectController.refresh();
    }
}
