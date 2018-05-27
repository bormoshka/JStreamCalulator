package ru.ulmc.generator.ui;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import ru.ulmc.generator.logic.Broadcaster;
import ru.ulmc.generator.logic.ConfigurationController;
import ru.ulmc.generator.logic.beans.Scenario;

public class UiUtils {
    public static void initScenarioComboBox(ComboBox<Scenario> scenarioCombo, ConfigurationController configurationController,
                                            Broadcaster broadcaster) {
        scenarioCombo.setConverter(new StringConverter<Scenario>() {
            @Override
            public String toString(Scenario object) {
                if (object == null) {
                    return "Не выбрано";
                }
                return object.getName();
            }

            @Override
            public Scenario fromString(String string) {
                if (string == null) {
                    return null;
                }
                return configurationController.getCurrentUserConfiguration().getScenarios().stream()
                        .filter(scenario -> scenario.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
            }
        });
        scenarioCombo.getItems().add(null);
        scenarioCombo.getItems().addAll(configurationController.getCurrentUserConfiguration().getScenarios());
        broadcaster.register(Scenario.class, new Broadcaster.BroadcastListener() {
            @Override
            public void receiveBroadcast(Broadcaster.BroadcastEvent message) {
                Scenario selected = scenarioCombo.getSelectionModel().getSelectedItem();
                scenarioCombo.getItems().clear();
                scenarioCombo.getItems().addAll(configurationController.getCurrentUserConfiguration().getScenarios());
                scenarioCombo.getSelectionModel().select(selected);
            }
        });
    }
}
