package ru.ulmc.generator.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.generator.logic.beans.ScenarioStep;
import ru.ulmc.generator.logic.beans.Trend;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ScenarioStepCell extends ListCell<ScenarioStep> {
    private static Gson gson = new GsonBuilder().serializeNulls().create();
    private static Image up;
    private static Image down;
    private static Image hold;
    private final Font font = Font.font(11);
    private Button remove;
    private ScenarioStepCell thisCell;
    private TextField stepFld = getField();
    private TextField volatilityFld = getField();
    private TextField durationFld = getField();
    private ComboBox<Trend> trendCombo = getComboBox();
    private ComboBox<TimeUnit> unitsCombo = getComboBox();

    public ScenarioStepCell() {
        if (up == null) {
            up = new Image(getClass().getResourceAsStream("up.png"));
            down = new Image(getClass().getResourceAsStream("down.png"));
            hold = new Image(getClass().getResourceAsStream("hold.png"));
        }
        thisCell = this;
        unitsCombo.getItems().addAll(TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS);
        unitsCombo.setPrefWidth(130);
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

        setOnDragDetected(event -> {
            ScenarioStep item = getItem();
            if (item == null) {
                return;
            }
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(gson.toJson(item));
            log.debug("Serialized value {}", content.getString());
            dragboard.setContent(content);
            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            ScenarioStep item = getItem();
            if (item == null) {
                return;
            }
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                ObservableList<ScenarioStep> items = getListView().getItems();

                ScenarioStep cellState = gson.fromJson(db.getString(), ScenarioStep.class);
                log.debug("Deserialized value {}", cellState);
                log.debug("Switching with {}", item);

                int draggedIdx = items.indexOf(cellState);
                int thisIdx = items.indexOf(item);

                items.set(draggedIdx, item);
                items.set(thisIdx, cellState);

                //List<ScenarioStep> itemscopy = new ArrayList<>(getListView().getItems());
                //  getListView().getItems().setAll(itemscopy);

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    private TextField getField() {
        TextField tf = new TextField();
        tf.setPrefWidth(45);
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
    protected void updateItem(ScenarioStep step, boolean empty) {
        super.updateItem(step, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        HBox cellBox;
        ImageView imageView = new ImageView(getImage(getItem()));
        stepFld.setText(String.valueOf(getItem().getDelta()));
        stepFld.setOnKeyReleased(event -> getItem().setDelta(getDouble(stepFld)));
        volatilityFld.setText(String.valueOf(getItem().getVolatility()));
        volatilityFld.setOnKeyReleased(event -> getItem().setVolatility(getDouble(volatilityFld)));
        durationFld.setText(String.valueOf(getItem().getDuration()));
        durationFld.setOnKeyReleased(event -> getItem().setDuration((int) getDouble(durationFld)));
        trendCombo.getSelectionModel().select(getItem().getTrend());
        trendCombo.setOnAction(event -> {
            getItem().setTrend(trendCombo.getValue());
            imageView.setImage(getImage(getItem()));
        });
        unitsCombo.getSelectionModel().select(getItem().getTimeUnits());
        unitsCombo.setOnAction(event -> {
            getItem().setTimeUnits(unitsCombo.getValue());
        });
        remove = new Button("X");
        remove.setPrefWidth(15);
        remove.setMaxHeight(18);
        remove.setFont(Font.font(remove.getFont().getFamily(), FontWeight.BOLD, 10));
        remove.setTextFill(Paint.valueOf("red"));
        remove.setOnAction(event -> {
            thisCell.getListView().getItems().remove(getItem());
        });
        //  remove.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        cellBox = new HBox(imageView,
                new Label("Дельта:"), stepFld, new Label("Волатильность:"), volatilityFld, new Label("Длительность:"),
                durationFld, unitsCombo, trendCombo, remove);
        cellBox.setSpacing(10);
        cellBox.setAlignment(Pos.CENTER_LEFT);
        setGraphic(cellBox);
    }

    private Image getImage(ScenarioStep step) {
        return step.getTrend() == Trend.HOLD ? hold : (step.getTrend() == Trend.UP ? up : down);
    }
}
