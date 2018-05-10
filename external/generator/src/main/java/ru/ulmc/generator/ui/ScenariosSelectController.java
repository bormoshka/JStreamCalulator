package ru.ulmc.generator.ui;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.generator.logic.ConfigurationController;
import ru.ulmc.generator.logic.beans.Scenario;
import ru.ulmc.generator.ui.views.ScenarioEditorView;

@FXMLController
@Slf4j
public class ScenariosSelectController {
    private final ConfigurationController configurationController;

    private final ViewManager viewManager;
    @FXML
    private ListView<Scenario> listView;
    @FXML
    private Button closeBtn;
    @FXML
    private Button editBtn;


    @Autowired
    public ScenariosSelectController(ConfigurationController configurationController, ViewManager viewManager) {
        this.configurationController = configurationController;
        this.viewManager = viewManager;
    }

    public void initialize() {
        closeBtn.setOnAction(event -> onClose(((Node) event.getSource()).getScene().getWindow()));

        editBtn.setOnAction(event -> {
            ScenarioEditorView.currentScenario = listView.getSelectionModel().getSelectedItem();
            viewManager.open(ScenarioEditorView.class, Modality.APPLICATION_MODAL, "Editing scenario");
        });
        listView.setCellFactory(param -> new ListCell<Scenario>() {
            @Override
            protected void updateItem(Scenario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new Label(item.getName() + " (steps: " + item.getSteps().size() + ")"));
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                if (selected) {
                    editBtn.setDisable(!selected);
                }
            }
        });
        // refresh();
    }

    public void onClose(Window window) {
        window.hide();
    }

    public void refresh() {
        listView.getItems().clear();
        listView.getItems().addAll(configurationController.getCurrentUserConfiguration().getScenarios());
    }

    @FXML
    public void newScenario() {
        ScenarioEditorView.currentScenario = null;
        viewManager.open(ScenarioEditorView.class, Modality.APPLICATION_MODAL, "Creating new scenario");
    }

}
