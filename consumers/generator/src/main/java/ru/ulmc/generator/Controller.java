package ru.ulmc.generator;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ulmc.generator.logic.ConfigurationController;
import ru.ulmc.generator.logic.PublishingController;
import ru.ulmc.generator.logic.StreamController;
import ru.ulmc.generator.logic.beans.QuoteEntity;
import ru.ulmc.generator.logic.beans.UserConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@FXMLController
@Slf4j
public class Controller {
    private final PublishingController publishingController;
    private final StreamController streamController;
    private final ConfigurationController configurationController;
    private final MainView view;

    @FXML
    private TextArea output;
    @FXML
    private TextField symbolField;
    @FXML
    private TextField bidField;
    @FXML
    private TextField offerField;
    @FXML
    private Button streamBtn;
    @FXML
    private Button sendBtn;
    @FXML
    private ToggleButton muteToggle;
    @FXML
    private VBox streamList;
    @FXML
    private TitledPane streamsPane;
    @FXML
    private MenuItem openItem;
    @FXML
    private MenuItem saveItem;
    @FXML
    private MenuItem saveAsItem;
    @FXML
    private MenuItem aboutItem;
    @FXML
    private MenuItem quitItem;
    @FXML
    private Menu recentMenu;
    @FXML
    private ScrollPane streamsWrapper;
    @FXML
    private Spinner<Double> interval;

    private Map<String, StreamRow> streamRows = new HashMap<>();
    private boolean isStreamOdd = false;

    @Autowired
    public Controller(PublishingController publishingController, StreamController streamController, ConfigurationController configurationController, MainView view) {
        this.publishingController = publishingController;
        this.streamController = streamController;
        this.configurationController = configurationController;
        this.view = view;
    }

    public void initialize() {
        initMenuItems();
        initRecentMenu();
        muteToggle.setOnAction(event -> {
            boolean selected = muteToggle.isSelected();
            streamController.setStreamMuted(selected);
            //  muteToggle.setText(selected ? "Mute streams" : "Mute streams");
        });
        streamController.setMessageConsumer(s -> {
            Platform.runLater(() -> log(s));
        });
        configurationController.setMessageConsumer(s -> {
            Platform.runLater(() -> log(s));
        });
        output.setEditable(false);
        streamList.setFillWidth(true);
        streamsWrapper.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        streamsWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        MenuItem clearItem = new MenuItem("Clear");
        clearItem.setOnAction(event -> {
            output.setText("");
        });
        ContextMenu contextMenu = new ContextMenu(clearItem);
        output.setContextMenu(contextMenu);
        interval.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 60, 3.0, 0.5));
        bidField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        offerField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        symbolField.setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object == null ? "" : object;
            }

            @Override
            public String fromString(String string) {
                return string == null ? "" : string.toUpperCase();
            }
        }));
        initStreamCreation();
    }

    private void initRecentMenu() {
        recentMenu.getItems().clear();
        configurationController.getRecentFilesExceptCurrent().forEach(file -> {
            MenuItem item = new MenuItem(file.getName());
            item.setOnAction(event -> configLoadingHandler(file));
            recentMenu.getItems().add(item);
        });
    }

    private void initMenuItems() {
        openItem.setOnAction(event -> openLoadDialog(this::configLoadingHandler));
        saveAsItem.setOnAction(event -> openSaveDialog(this::saveAs));
        saveItem.setOnAction(event -> openSaveDialog(file -> {
            if (file == null) {
                return;
            }
            if (configurationController.isSaveFileSet()) {
                saveAs(configurationController.getLastUsedFile());
            } else {
                saveAs(file);
            }
        }));
        quitItem.setOnAction(event -> {
            configurationController.saveAppConfig();
            Platform.exit();
            System.exit(0);
        });
    }

    private void configLoadingHandler(File file) {
        try {
            applyConfig(configurationController.load(file));
        } catch (Exception e) {
            log.error("Can't open file", e);
            log("Ошибка открытия файла");
        }
    }

    private void applyConfig(UserConfiguration cfg) {
        if (cfg == null) {
            return;
        }
        symbolField.setText(cfg.getSymbol());
        bidField.setText(String.valueOf(cfg.getBid()));
        offerField.setText(String.valueOf(cfg.getOffer()));
        interval.getValueFactory().setValue(cfg.getInterval());
        streamList.getChildren().clear();
        cfg.getStreamTasks().forEach(task -> {
            insertNewStreamRow(task.getSymbol(), task.getBid(), task.getOffer(), task.getInterval());
        });
        initRecentMenu();
    }

    private UserConfiguration collectConfig() {
        return UserConfiguration.builder()
                .symbol(symbolField.getText())
                .bid(getBid())
                .offer(getOffer())
                .interval(interval.getValue())
                .streamTasks(streamRows.values().stream().map(StreamRow::getTask).collect(Collectors.toList()))
                .build();
    }

    private void saveAs(File file) {
        try {
            configurationController.save(file, collectConfig());
            initRecentMenu();
        } catch (Exception e) {
            log.error("Can't open file", e);
            log("Ошибка сохранения файла");
        }
    }

    private void openLoadDialog(Consumer<File> fileConsumer) {
        FileChooser fileChooser = getFileChooser("Open Config File");
        fileConsumer.accept(fileChooser.showOpenDialog(Generator.getStage()));
    }

    private void openSaveDialog(Consumer<File> fileConsumer) {
        FileChooser fileChooser = getFileChooser("Save config file as");
        fileConsumer.accept(fileChooser.showSaveDialog(Generator.getStage()));
    }

    private FileChooser getFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(".//"));
        fileChooser.getExtensionFilters().addAll(getExtensionFilter());
        return fileChooser;
    }

    private FileChooser.ExtensionFilter[] getExtensionFilter() {
        return new FileChooser.ExtensionFilter[]{
                new FileChooser.ExtensionFilter("Generator config file", "*.gcfg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        };
    }

    private void initStreamCreation() {
        //streamList.setSpacing(10);
        streamBtn.setOnAction(event -> {
            try {
                String symbol = symbolField.getText();
                if (streamRows.keySet().contains(symbol)) {
                    log.debug("Stream for {} already exist", symbol);
                    return;
                }
                double bid = getBid();
                double offer = getOffer();
                Double interval = this.interval.getValue();
                insertNewStreamRow(symbol, bid, offer, interval);

                // streamController.startNewTask(symbol, bid, offer, 0, interval);
            } catch (Exception ex) {
                log.error("Exception on creating new stream", ex);
                log("Ошибка при создании потока, см. логи");
            }
        });
    }

    private void insertNewStreamRow(String symbol, double bid, double offer, Double interval) {
        StreamRow sRow = new StreamRow(symbol, bid, offer, 0, interval);
        sRow.setPrefWidth(streamsPane.getPrefWidth());
        if (isStreamOdd) {
            sRow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#eee"), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        sRow.initReschedule(streamController::reschedule);
        ToggleButton toggleBtn = sRow.getToggleBtn();
        toggleBtn.setOnAction(tEvent -> {
            if (toggleBtn.isSelected()) {
                sRow.toggleOn();
                streamController.startNewTask(symbol, bid, offer, 0, interval);
            } else {
                sRow.toggleOff();
                streamController.stopStreaming(symbol);
            }
        });
        sRow.getRemove().setOnAction(event1 -> {
            streamRows.remove(symbol);
            streamController.stopStreaming(symbol);
            streamList.getChildren().remove(sRow);
        });
        isStreamOdd = !isStreamOdd;
        streamRows.put(symbol, sRow);

        streamList.getChildren().add(sRow);
    }


    @FXML
    private void tryToEnableButtons() {
        boolean valid = validate();
        sendBtn.setDisable(!valid);
        streamBtn.setDisable(!valid);
    }

    private boolean validate() {
        String symbol = symbolField.getText();
        if (!symbol.contains("/")) {
            return false;
        }
        try {
            String bidStr = bidField.getText();
            String offerStr = offerField.getText();
            if (bidStr == null || bidStr.isEmpty() || offerStr == null || offerStr.isEmpty()) {
                return false;
            }
            double bid = getBid();
            double offer = getOffer();
            return bid > 0 && offer > bid;
        } catch (Exception ignore) {
        }

        return true;
    }

    @FXML
    private void sendQuote() {
        String symbol = symbolField.getText();
        if (symbol == null || symbol.trim().isEmpty()) {
            log("Не задано значение валютной пары");
            return;
        }
        String bidStr = bidField.getText();
        String offerStr = offerField.getText();
        Double bid;
        Double offer;
        try {
            bid = Double.parseDouble(bidStr);
            offer = Double.parseDouble(offerStr);
        } catch (Exception ex) {
            log.error("Parse error", ex);
            log("ошибка распозноваия значений покупки/продажи");
            return;
        }
        try {
            QuoteEntity entity = new QuoteEntity(symbol, BigDecimal.valueOf(bid), BigDecimal.valueOf(offer));
            log("Отправка на публикацию: " + entity);
            publishingController.publish(entity);
            log("Отправлено");
        } catch (Exception ex) {
            log.error("Publishing failed!", ex);
            log("Ошибка публикации: " + ex.getMessage());
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

    private double getOffer() {
        try {
            return Double.parseDouble(offerField.getText());
        } catch (Exception ex) {
            log.error("Parse error", ex);
            throw new UserInputException("Ошибка распозноваия значений продажи [" + offerField.getText() + "]");
        }
    }

    private void log(String msg) {
        double top =output.getScrollTop();
        output.setText(output.getText() + "\n" + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + ": " + msg);
        output.setScrollTop(top+20);
    }
}
