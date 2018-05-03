package ru.ulmc.generator.ui;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.generator.logic.Broadcaster;
import ru.ulmc.generator.logic.ConfigurationController;
import ru.ulmc.generator.logic.beans.Scenario;
import ru.ulmc.generator.logic.beans.ScenarioStep;
import ru.ulmc.generator.logic.beans.Trend;
import ru.ulmc.generator.ui.views.ScenarioEditorView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@FXMLController
@Slf4j
public class ScenarioEditorController {
    private final ConfigurationController configurationController;
    private final ViewManager viewManager;
    private final Broadcaster broadcaster;
    @FXML
    private Button closeBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button createBtn;
    @FXML
    private TextField stepFld;
    @FXML
    private TextField scenarioName;
    @FXML
    private TextField volatilityFld;
    @FXML
    private TextField durationFld;
    @FXML
    private ComboBox<Trend> trendCombo;
    @FXML
    private ComboBox<TimeUnit> unitsCombo;
    @FXML
    private ListView<ScenarioStep> listView;
    private Scenario scenario;


    @Autowired
    public ScenarioEditorController(ConfigurationController configurationController, ViewManager viewManager, Broadcaster broadcaster) {
        this.configurationController = configurationController;
        this.viewManager = viewManager;
        this.broadcaster = broadcaster;
    }

    private void clearData() {
        if (listView == null) {
            return;
        }
        listView.getItems().clear();
        stepFld.setText("");
        volatilityFld.setText("");
        durationFld.setText("");
    }

    public void initialize() {
        closeBtn.setOnAction(event -> ((Node) (event.getSource())).getScene().getWindow().hide());
        unitsCombo.getItems().addAll(TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS);
        unitsCombo.getSelectionModel().select(TimeUnit.SECONDS);
        unitsCombo.setConverter(new StringConverter<TimeUnit>() {
            @Override
            public String toString(TimeUnit object) {
                return object.name();
            }

            @Override
            public TimeUnit fromString(String string) {
                return TimeUnit.valueOf(string);
            }
        });
        trendCombo.getItems().addAll(Trend.UP, Trend.HOLD, Trend.DOWN);
        trendCombo.getSelectionModel().select(Trend.HOLD);
        trendCombo.setConverter(new StringConverter<Trend>() {
            @Override
            public String toString(Trend object) {
                return object.name();
            }

            @Override
            public Trend fromString(String string) {
                return Trend.valueOf(string);
            }
        });
        addBtn.setOnAction(event -> {
            if (!validateFields()) return;
            String step = stepFld.getText();
            String volatility = volatilityFld.getText();
            String duration = durationFld.getText();
            Trend trend = trendCombo.getValue();
            addScenario(step, volatility, duration, trend, unitsCombo.getValue());
        });
        createBtn.setOnAction(event -> {
            scenario.setName(scenarioName.getText());
            scenario.setSteps(new ArrayList<>(listView.getItems())); // keep it all in order
            List<Scenario> scenarios = configurationController.getCurrentUserConfiguration().getScenarios();
            boolean existing = scenarios.contains(scenario);
            if (existing) {
                scenarios.remove(scenario);
            }
                scenarios.add(scenario);
            broadcaster.broadcast(new Broadcaster.BroadcastEvent<>(scenario));
            ((Node) (event.getSource())).getScene().getWindow().hide();
        });
        listView.setCellFactory(view -> new ScenarioStepCell());

    }

    public void onShow() {
        clearData();
        scenario = ScenarioEditorView.currentScenario;
        if (scenario == null) {
            scenario = ScenarioEditorView.currentScenario = new Scenario("");
        }
        if (scenario.getSteps() != null) {
            listView.getItems().setAll(scenario.getSteps());
        }
        scenarioName.setText(scenario.getName());
        tryToEnableSaveBth();
    }

    @FXML
    public void tryToEnableAddBtn() {
        addBtn.setDisable(!validateFields());
    }

    @FXML
    public void tryToEnableSaveBth() {
        String text = scenarioName.getText();
        createBtn.setDisable(!(text != null && !text.isEmpty()));
    }


    private boolean validateFields() {
        String step = stepFld.getText();
        String volatility = volatilityFld.getText();
        String duration = durationFld.getText();
        Trend trend = trendCombo.getValue();
        boolean valid = step != null && !step.isEmpty();
        valid = valid && volatility != null && !volatility.isEmpty();
        valid = valid && duration != null && !duration.isEmpty();
        valid = valid && trend != null;
        return valid;
    }

    private void addScenario(String step, String volatility, String duration, Trend trend, TimeUnit tUnit) {
        ScenarioStep scenarioStep = scenario.addStep(trend, Integer.valueOf(duration), Double.valueOf(step),
                Double.parseDouble(volatility), tUnit);
        listView.getItems().add(scenarioStep);
    }

    @FXML
    public void newScenario() {

    }

}
