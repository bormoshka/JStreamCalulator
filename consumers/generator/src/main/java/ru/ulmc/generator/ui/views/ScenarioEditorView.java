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
public class ScenarioEditorView extends CommonView {
    public static Scenario currentScenario;

    private final ScenariosSelectController scenariosSelectController;
    private final ScenarioEditorController scenarioEditorController;

    @Autowired
    public ScenarioEditorView(ScenariosSelectController scenariosSelectController,
                              ScenarioEditorController scenarioEditorController) {
        super();
        this.scenariosSelectController = scenariosSelectController;
        this.scenarioEditorController = scenarioEditorController;
    }

    @Override
    public EventHandler<WindowEvent> getOnHideHandler() {
        return event -> scenariosSelectController.refresh();
    }

    @Override
    public EventHandler<WindowEvent> getOnSnowHandler() {
        return event -> scenarioEditorController.onShow();
    }
}
