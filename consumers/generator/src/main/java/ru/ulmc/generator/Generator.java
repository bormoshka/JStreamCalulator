package ru.ulmc.generator;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import ru.ulmc.generator.logic.StreamController;

@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        LdapAutoConfiguration.class})
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

    @Override
    public void stop() throws Exception {
        super.stop();

    }
}
