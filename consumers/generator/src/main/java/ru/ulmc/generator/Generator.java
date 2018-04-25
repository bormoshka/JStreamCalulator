package ru.ulmc.generator;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Generator extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(Generator.class, MainView.class, new SplashScreen() {
            @Override
            public boolean visible() {
                return false;
            }
        }, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }
}
