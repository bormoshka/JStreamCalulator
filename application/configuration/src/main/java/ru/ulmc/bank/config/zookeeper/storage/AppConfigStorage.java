package ru.ulmc.bank.config.zookeeper.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.utils.CloseableUtils;
import ru.ulmc.bank.config.zookeeper.ConfigPath;
import ru.ulmc.bank.config.zookeeper.Result;
import ru.ulmc.bank.config.zookeeper.ZooConfigMonitor;
import ru.ulmc.bank.core.common.exception.ConfigurationException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AppConfigStorage implements Closeable {

    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private final ZooConfigMonitor<String> configMonitor;
    private boolean isInitialized = false;

    public AppConfigStorage(@NonNull String zooConnection) throws Exception {
        log.info("Zoo symbols service was initialized {}", zooConnection);
        try {
            configMonitor = new ZooConfigMonitor<String>(zooConnection, ConfigPath.CONFIG.getPath(), ConfigPath.CONFIG.getEntityClass(), properties);
        } catch (IOException e) {
            log.error("Zoo symbols service initialization crashed {}", zooConnection, e);
            throw e;
        }
    }

    public Map<String, String> getProperties() {
        if (isInitialized) {
            return properties;
        }
        configMonitor.readAllUnmodified().forEach(stringResult -> properties.put(stringResult.getNodeId(), stringResult.getData()));

        isInitialized = !isInitialized;
        return properties;
    }

    public String save(String name, String value) {
        Result<String> result = configMonitor.save(name, value);
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Error to save currency " + name);
        }
        return result.getData();
    }

    @Override
    public void close() {
        CloseableUtils.closeQuietly(configMonitor);
    }
}
