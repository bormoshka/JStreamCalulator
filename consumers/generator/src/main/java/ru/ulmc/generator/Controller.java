package ru.ulmc.generator;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@FXMLController
@Slf4j
public class Controller {
    private final PublishingController publishingController;
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
    private ToggleButton streamToggle;
    @FXML
    private Button sendBtn;
    @FXML
    private Label appTitle;

    @Autowired
    public Controller(PublishingController publishingController, MainView view) {
        this.publishingController = publishingController;
        this.view = view;
    }

    public void initialize() {
        output.setEditable(false);
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
            QuoteEntity entity = new QuoteEntity(symbol, bid, offer);
            log("Отправка на публикацию: " + entity);
            publishingController.publish(entity);
            log("Отправлено");
        } catch (Exception ex) {
            log.error("Publishing failed!", ex);
            log("Ошибка публикации: " + ex.getMessage());
        }
    }

    private void log(String msg) {
        output.setText(output.getText() + "\n" + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + ": " + msg);
    }
}
