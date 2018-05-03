package ru.ulmc.generator.ui;

import javafx.collections.ObservableListBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.generator.UserInputException;
import ru.ulmc.generator.logic.Broadcaster;
import ru.ulmc.generator.logic.ConfigurationController;
import ru.ulmc.generator.logic.ScenarioProcess;
import ru.ulmc.generator.logic.beans.Scenario;
import ru.ulmc.generator.logic.beans.StreamTask;

import java.util.function.Consumer;

@Slf4j
public class StreamRow extends HBox {
    private final ImageView img;
    private String symbol;
    private Label symbolLabel;
    private TextField bidField;
    private TextField offerField;
    private TextField volatilityField;
    private Spinner<Double> intervalSpinner;
    @Getter
    private ToggleButton toggleBtn;
    @Getter
    private Button remove;
    private ComboBox<Scenario> scenarios;
    private Image green;
    private Image red;

    public StreamRow(Broadcaster broadcaster, ConfigurationController configurationController, String symbol,
                     double bid, double offer, double volatility, double interval, Scenario scenario) {
        this.symbol = symbol;
        img = new ImageView();
        img.setSmooth(true);
        img.setFitWidth(24);
        green = new Image(getClass().getResourceAsStream("greendot.png"));
        red = new Image(getClass().getResourceAsStream("reddot.png"));

        symbolLabel = new Label(symbol);
        symbolLabel.setMinWidth(100);
        symbolLabel.setMaxWidth(150);
        symbolLabel.setAlignment(Pos.CENTER);
        symbolLabel.setFont(Font.font(16));
        intervalSpinner = new Spinner<>(0.5d, 60d, interval, 0.5d);
        intervalSpinner.setPrefWidth(60);
        toggleBtn = new ToggleButton("OFF");
        toggleBtn.setSelected(false);
        toggleBtn.setPrefWidth(40);

        bidField = createField(bid);
        offerField = createField(offer);
        volatilityField = createField(volatility);

        Label bidLabel = new Label();
        bidLabel.setText("bid:");
        bidLabel.setLabelFor(bidField);
        Label offerLabel = new Label();
        offerLabel.setText("offer:");
        offerLabel.setLabelFor(offerField);
        Label volLabel = new Label();
        volLabel.setText("vol:");
        volLabel.setLabelFor(volatilityField);

        Label intLabel = new Label();
        intLabel.setText("per:");
        intLabel.setLabelFor(intervalSpinner);
        scenarios = new ComboBox<>();
        UiUtils.initScenarioComboBox(scenarios, configurationController, broadcaster);
        if (scenario != null) {
            scenarios.getSelectionModel().select(scenario);
        }
        remove = new Button("X");
        remove.setPrefWidth(15);
        remove.setFont(Font.font(remove.getFont().getFamily(), FontWeight.BOLD, 14));
        remove.setTextFill(Paint.valueOf("red"));
        remove.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5));
        setHeight(50);
        getChildren().addAll(img, symbolLabel, bidLabel, bidField, offerLabel, offerField, /*volLabel, volatilityField,*/
                intLabel, intervalSpinner, scenarios, toggleBtn, remove);
        //setMinWidth(250);
        toggleOff();
    }

    public void initReschedule(Consumer<StreamTask> consumer, Consumer<ScenarioProcess> scenarioConsumer) {
        intervalSpinner.valueProperty().addListener((obs, oldValue, newValue) -> giveTaskToConsumer(consumer, scenarioConsumer));
        bidField.setOnKeyPressed(event -> giveTaskToConsumer(consumer, scenarioConsumer));
        offerField.setOnKeyPressed(event -> giveTaskToConsumer(consumer, scenarioConsumer));
    }

    private void giveTaskToConsumer(Consumer<StreamTask> consumer, Consumer<ScenarioProcess> scenarioConsumer) {
        if (!toggleBtn.isSelected()) {
            return;
        }
        ScenarioProcess process = getScenarioProcess();
        if (process != null) {
            scenarioConsumer.accept(process);
        }
        StreamTask task = getTask();
        if (task != null) {
            consumer.accept(task);
        }
    }

    public ScenarioProcess getScenarioProcess() {
        ScenarioProcess process = null;
        Scenario scenario = scenarios.getValue();

        if (scenario != null) {
            intervalSpinner.setDisable(true);
            bidField.setDisable(true);
            offerField.setDisable(true);
            scenarios.setDisable(true);
            process = new ScenarioProcess(symbol, getBid(), getOffer(), intervalSpinner.getValue(), scenario);
        }
        return process;
    }

    StreamTask getTask() {
        try {
            return new StreamTask(symbol, getBid(), getOffer(), 0, intervalSpinner.getValue(), scenarios.getValue());
        } catch (Exception ex) {
            return null;
        }
    }

    private double getBid() {
        try {
            return Double.parseDouble(bidField.getText());
        } catch (Exception ex) {
            log.error("Parse error", ex);
            throw new UserInputException("Ошибка распозноваия значений покупки [" + bidField.getText() + "]");
        }
    }

    private double getVolatility() {
        try {
            return Double.parseDouble(volatilityField.getText());
        } catch (Exception ex) {
            log.error("Parse error", ex);
            throw new UserInputException("Ошибка распозноваия значений покупки [" + bidField.getText() + "]");
        }
    }

    private double getOffer() {
        try {
            return Double.parseDouble(offerField.getText());
        } catch (Exception ex) {
            log.error("Parse error", ex);
            throw new UserInputException("Ошибка распозноваия значений продажи [" + offerField.getText() + "]");
        }
    }

    private TextField createField(double value) {
        TextField field = new TextField();
        field.setPrefWidth(50);
        field.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        field.setText(String.valueOf(value));
        return field;
    }

    public void toggleOn() {
        toggleBtn.setText("ON");
        img.setImage(green);
        // toggleBtn.setBackground(
        //         new Background(new BackgroundFill(
        //                 new Color(.1, 0.7, 0.1, 0.7d), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public void toggleOff() {
        toggleBtn.setText("OFF");
        intervalSpinner.setDisable(false);
        bidField.setDisable(false);
        offerField.setDisable(false);
        scenarios.setDisable(false);
        img.setImage(red);
        //  toggleBtn.setBackground(
        //          new Background(new BackgroundFill(
        //                  new Color(0.7, 0.1, 0.1, 0.7d), CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
