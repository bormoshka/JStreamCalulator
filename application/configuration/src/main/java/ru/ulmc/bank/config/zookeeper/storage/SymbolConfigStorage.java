package ru.ulmc.bank.config.zookeeper.storage;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.utils.CloseableUtils;
import ru.ulmc.bank.config.zookeeper.ConfigPath;
import ru.ulmc.bank.config.zookeeper.Result;
import ru.ulmc.bank.config.zookeeper.ZooConfigMonitor;
import ru.ulmc.bank.core.common.exception.ConfigurationException;
import ru.ulmc.bank.config.zookeeper.entities.Currency;
import ru.ulmc.bank.config.zookeeper.entities.SymbolConfig;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SymbolConfigStorage implements Closeable {

    private final Map<String, Currency> mapCurrencies = new ConcurrentHashMap<>();
    private final Map<String, SymbolConfig> mapSymbols = new ConcurrentHashMap<>();
    private final ZooConfigMonitor<Currency> currencyMonitor;
    private final ZooConfigMonitor<SymbolConfig> symbolMonitor;
    private boolean caching;

    public SymbolConfigStorage(@NonNull String zooConnection, boolean caching) throws Exception {
        this(zooConnection);
        this.caching = caching;
    }

    public SymbolConfigStorage(@NonNull String zooConnection) throws Exception {
        log.info("Zoo symbols service was initialized {}", zooConnection);
        try {
            currencyMonitor = new ZooConfigMonitor<Currency>(zooConnection, ConfigPath.CURRENCY.getPath(), ConfigPath.CURRENCY.getEntityClass(), mapCurrencies);
            symbolMonitor = new ZooConfigMonitor<SymbolConfig>(zooConnection, ConfigPath.SYMBOL.getPath(), ConfigPath.SYMBOL.getEntityClass(), mapSymbols);
        } catch (IOException e) {
            log.error("Zoo symbols service initialization crashed {}", zooConnection, e);
            throw e;
        }
    }

    public List<Currency> getCurrencies() {
        if (caching) {
            return new ArrayList<>(mapCurrencies.values());
        }

        Result<List<Currency>> currencies = currencyMonitor.readAll();
        if (currencies.getErrorMessage() != null) {
            throw new ConfigurationException("Error to read currencies");
        }
        return currencies.getData();
    }

    public Currency getCurrency(@NonNull String iso) {
        if (caching) {
            return mapCurrencies.get(iso);
        }

        Result<Currency> result = currencyMonitor.read(iso);
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Error to get currency " + iso);
        }
        return result.getData();
    }

    public Currency saveCurrency(@NonNull Currency currency) {
        Result<Currency> result = currencyMonitor.save(currency.getIso(), currency);
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Error to save currency " + currency.getIso());
        }
        return result.getData();
    }

    public List<Currency> saveCurrencies(@NonNull List<Currency> currs) {
        Map<String, Currency> savedCurrencies = currs.stream().collect(Collectors.toMap(Currency::getIso, k -> k));
        Result<List<Currency>> listResult = currencyMonitor.saveAll(savedCurrencies);
        if (listResult.getErrorMessage() != null) {
            throw new ConfigurationException("Error to save list of currencies ");
        }
        return listResult.getData();
    }

    public String removeCurrency(@NonNull String iso) {
        Result<Object> result = currencyMonitor.delete(iso);
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Cannot delete currency " + iso);
        }
        return (String) result.getData();
    }

    public List<SymbolConfig> getSymbolConfigs() {
        if (caching) {
            return new ArrayList<>(mapSymbols.values());
        }

        Result<List<SymbolConfig>> currencies = symbolMonitor.readAll();
        if (currencies.getErrorMessage() != null) {
            throw new ConfigurationException("Error to read SymbolConfigs");
        }
        return currencies.getData();
    }

    public SymbolConfig getSymbolConfig(@NonNull String symbol) {
        String key = convertSymbolKey(symbol);
        if (caching) {
            return mapSymbols.get(key);
        }

        Result<SymbolConfig> result = symbolMonitor.read(key);
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Error to get SymbolConfig " + key);
        }
        return result.getData();
    }

    public List<SymbolConfig> saveSymbolConfigs(@NonNull Collection<SymbolConfig> symbolConfigs) {
        Map<String, SymbolConfig> savedCurrencies = symbolConfigs.stream().collect(Collectors.toMap(o -> convertSymbolKey(o.getSymbol()), k -> k));
        Result<List<SymbolConfig>> listResult = symbolMonitor.saveAll(savedCurrencies);
        if (listResult.getErrorMessage() != null) {
            throw new ConfigurationException("Error to save list of currencies ");
        }
        return listResult.getData();
    }

    public String removeSymbolConfig(@NonNull String symbol) {
        Result<Object> result = symbolMonitor.delete(convertSymbolKey(symbol));
        if (result.getErrorMessage() != null) {
            throw new ConfigurationException("Cannot delete SymbolConfig " + symbol);
        }
        return (String) result.getData();
    }

    @Override
    public void close() {
        CloseableUtils.closeQuietly(currencyMonitor);
        CloseableUtils.closeQuietly(symbolMonitor);
    }

    private String convertSymbolKey(String symbol) {
        return symbol.replace("/", "_");
    }
}
