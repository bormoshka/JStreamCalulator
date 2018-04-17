package ru.ulmc.bank.compute;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.io.Serializable;
import java.util.Arrays;

public class IgniteQuoteService implements AutoCloseable, Serializable {
    private final Ignite ignite;

    private IgniteQuoteService(String host) {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(true);
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();

        ipFinder.setAddresses(Arrays.asList(host));
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);
        TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
        commSpi.setSlowClientQueueLimit(1000);

        cfg.setCommunicationSpi(commSpi);

        ignite = Ignition.getOrStart(cfg);
    }

    // private void configure() {
    //     CacheConfiguration cfg = new CacheConfiguration("BaseQuotes");
    //     cfg
    // }

    public static IgniteQuoteService open(String host) {
        return new IgniteQuoteService(host);
    }

    public Ignite ignite() {
        return ignite;
    }

    public IgniteCompute compute() {
        return ignite.compute();
    }

    public IgniteCompute compute(Object obj) {
        return ignite.compute();
    }

    @Override
    public void close() throws Exception {
        ignite.close();
    }
}
