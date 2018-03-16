package ru.ulmc.bank.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingServer;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import ru.ulmc.bank.config.zookeeper.storage.SymbolConfigStorage;
import ru.ulmc.bank.entities.configuration.Currency;
import ru.ulmc.bank.entities.configuration.SymbolConfig;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class ZooTests {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    SymbolConfigStorage strg;

    private String connectString;
    @Rule
    private TestingServer zkTestServer;

    @BeforeEach
    void init() throws Exception {
        tmpFolder.create();
        InstanceSpec newInstanceSpec = new InstanceSpec(tmpFolder.getRoot(), 2181, -1, -1, true, -1);
        zkTestServer = new TestingServer(newInstanceSpec, true);
        zkTestServer.start();
        connectString = zkTestServer.getConnectString();
    }


    @AfterEach
    void close() {
        try {
            strg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            zkTestServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tmpFolder.delete();
    }

    @Test
    public void test() {
        try {
            strg = new SymbolConfigStorage(connectString);
            strg.saveCurrency(new Currency("TST", true));
            strg.saveCurrency(new Currency("TST1", true));

            assertEquals(2, strg.getCurrencies().size());
            assertTrue(strg.getCurrency("TST").isActive());

            System.out.println("Save currencies OK");
            SymbolConfig sc = new SymbolConfig("TST/TST1", 0.5, 0.5);
            strg.saveSymbolConfigs(Collections.singletonList(sc));

            assertEquals("TST/TST1", strg.getSymbolConfig("TST/TST1").getSymbol());
            strg.removeSymbolConfig("TST/TST1");

            assertTrue(strg.getSymbolConfigs().isEmpty());
            System.out.println("SymbolConfigs OK");

            strg.removeCurrency("TST");
            strg.removeCurrency("TST1");
            assertTrue(strg.getCurrencies().isEmpty());
            System.out.println("CleanUp OK");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
