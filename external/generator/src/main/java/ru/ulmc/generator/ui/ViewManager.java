package ru.ulmc.generator.ui;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ru.ulmc.generator.Generator;
import ru.ulmc.generator.ui.views.CommonView;

@Component
public class ViewManager {
    private final ConfigurableApplicationContext applicationContext;

    public ViewManager(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void open(final Class<? extends CommonView> window, final Modality mode, String title) {
        final CommonView view = applicationContext.getBean(window);
        Stage newStage = new Stage();

        Scene newScene;
        if (view.getView().getScene() != null) {
            newScene = view.getView().getScene();
        } else {
            newScene = new Scene(view.getView());
        }

        newStage.setScene(newScene);
        newStage.initModality(mode);
        newStage.initOwner(Generator.getStage());
        newStage.setTitle(title);
        newStage.setResizable(false);
        newStage.initStyle(StageStyle.DECORATED);
        newStage.setOnHidden(view.getOnHideHandler());
        newStage.setOnShowing(view.getOnSnowHandler());
        newStage.showAndWait();
    }
}
