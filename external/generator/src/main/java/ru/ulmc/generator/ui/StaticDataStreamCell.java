package ru.ulmc.generator.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.generator.logic.StreamController;
import ru.ulmc.generator.logic.beans.StaticData;

@Slf4j
public class StaticDataStreamCell extends ListCell<StaticData> {
    private final Font font = Font.font(11);
    private Button remove;
    private TextField nameField = getField(200);
    private TextField symbolField = getField(85);
    private TextField intervalField = getField(45);
    private ToggleButton toggleBtn;
    private StaticDataStreamCell cell;
    private StreamController streamController;

    public StaticDataStreamCell(StreamController streamController) {
        this.streamController = streamController;
        cell = this;

        toggleBtn = new ToggleButton("OFF");
        toggleBtn.setOnAction(event -> {
            if (!toggleBtn.isSelected()) {
                toggleOff();
                streamController.stopStreaming(getItem().getSymbol());
            } else {
                toggleOn();
                StaticData sd = getItem();
                sd.setSymbol(symbolField.getText());
                sd.setInterval(Double.parseDouble(intervalField.getText()));
                streamController.startNewTask(getItem());
            }
        });
        toggleBtn.setSelected(false);
        toggleBtn.setPrefWidth(40);
    }

    private TextField getField(int width) {
        TextField tf = new TextField();
        tf.setPrefWidth(width);
        tf.setFont(font);
        return tf;
    }

    private <T> ComboBox<T> getComboBox() {
        ComboBox<T> cb = new ComboBox<>();
        cb.setPrefWidth(80);
        cb.getEditor().setFont(font);
        return cb;
    }

    private double getDouble(TextField tf) {
        try {
            return Double.parseDouble(tf.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    protected void updateItem(StaticData step, boolean empty) {
        super.updateItem(step, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        HBox cellBox;
        nameField.setText(step.getFileName());
        nameField.setOnKeyReleased(event -> getItem().setFileName(nameField.getText()));
        symbolField.setText(step.getSymbol());
        symbolField.setOnKeyReleased(event -> getItem().setSymbol(symbolField.getText()));
        intervalField.setText(step.getInterval() + "");
        intervalField.setOnKeyReleased(event -> {
            try {
                getItem().setInterval(Double.parseDouble(intervalField.getText()));
            } catch (Exception ex) {
            }
        });
        remove = new Button("X");
        remove.setPrefWidth(15);
        remove.setMaxHeight(18);
        remove.setFont(Font.font(remove.getFont().getFamily(), FontWeight.BOLD, 10));
        remove.setTextFill(Paint.valueOf("red"));
        remove.setOnAction(event -> {
            cell.getListView().getItems().remove(getItem());
        });
        if (step.isActive()) {
            toggleOn();
        } else {
            toggleOff();
        }
        cellBox = new HBox(new Label("Имя:"), nameField,
                new Label("ВП:"), symbolField,
                new Label("Интервал:"),
                intervalField, toggleBtn, remove);
        cellBox.setSpacing(10);
        cellBox.setAlignment(Pos.CENTER_LEFT);
        setGraphic(cellBox);
    }

    public void toggleOn() {
        toggleBtn.setText("ON");
        getItem().setActive(true);
        symbolField.setDisable(true);
        intervalField.setDisable(true);
    }

    public void toggleOff() {
        toggleBtn.setText("OFF");
        getItem().setActive(false);
        symbolField.setDisable(false);
        intervalField.setDisable(false);
    }
}
