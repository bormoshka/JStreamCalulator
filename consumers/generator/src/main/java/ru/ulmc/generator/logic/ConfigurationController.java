package ru.ulmc.generator.logic;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.ulmc.generator.logic.beans.AppConfiguration;
import ru.ulmc.generator.logic.beans.UserConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ConfigurationController {
    private final static String APP_CFG_FILENAME = ".//settings.acfg";
    private final StreamController controller;
    @Getter
    private UserConfiguration currentUserConfiguration = null;
    @Getter
    private AppConfiguration appConfiguration = new AppConfiguration();
    private ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(AppConfiguration.class, new AppConfigurationSerializer());
        return kryo;
    });
    @Getter
    private File lastUsedFile;

    @Autowired
    public ConfigurationController(StreamController controller) {
        this.controller = controller;
    }
    @Setter
    private Consumer<String> messageConsumer;

    @PostConstruct
    public void loadAppConfig() {
        try (Input input = new Input(new FileInputStream(new File(APP_CFG_FILENAME)))) {
            appConfiguration = (AppConfiguration) kryo.get().readClassAndObject(input);
        } catch (Exception ignore) {
            log.info("App config not found. It's OK");
            File conf = new File(APP_CFG_FILENAME);
            if (conf.exists()) {
                conf.renameTo(new File(APP_CFG_FILENAME + "_backup"));
            }
        }
    }

    @PreDestroy
    public void saveAppConfig() {
        try (Output output = new Output(new FileOutputStream(new File(APP_CFG_FILENAME)))) {
            kryo.get().writeClassAndObject(output, appConfiguration);
        } catch (FileNotFoundException e) {
            log.error("App config not found. It's NOT OK", e);
        }
    }

    public Set<File> getRecentFilesExceptCurrent() {
        if (lastUsedFile == null) {
            return appConfiguration.getRecentFiles().stream().limit(10).collect(Collectors.toSet());
        }
        return appConfiguration.getRecentFiles().stream().filter(file -> !lastUsedFile.equals(file))
                .limit(10)
                .collect(Collectors.toSet());
    }

    public UserConfiguration load(File file) throws FileNotFoundException {
        if (file == null) {
            log.debug("Skip config loading");
            return null;
        }
        try (Input input = new Input(new FileInputStream(file))) {
            currentUserConfiguration = (UserConfiguration) kryo.get().readClassAndObject(input);
            updateFileLinks(file);
        }
        controller.stopStreaming();
        messageConsumer.accept("New configuration is loaded");
        return currentUserConfiguration;
    }

    public boolean isSaveFileSet() {
        return lastUsedFile != null;
    }

    public void save(File file, UserConfiguration newConfig) throws FileNotFoundException {
        if (file == null) {
            log.debug("Skip config saving");
            return;
        }
        try (Output output = new Output(new FileOutputStream(file))) {
            currentUserConfiguration = newConfig;
            kryo.get().writeClassAndObject(output, currentUserConfiguration);
            output.flush();
        }
        updateFileLinks(file);

    }

    private void updateFileLinks(File file) {
        lastUsedFile = file;
        appConfiguration.getRecentFiles().add(file);
        //saveAppConfig(); //Really? Here!?
    }
}
