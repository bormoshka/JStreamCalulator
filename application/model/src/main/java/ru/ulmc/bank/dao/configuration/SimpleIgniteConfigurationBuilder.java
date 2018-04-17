package ru.ulmc.bank.dao.configuration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.hibernate.ogm.datastore.ignite.IgniteConfigurationBuilder;

import java.util.Arrays;

public class SimpleIgniteConfigurationBuilder implements IgniteConfigurationBuilder {
    @Override
    public IgniteConfiguration build() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(true);
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryZookeeperIpFinder zooConfig = new TcpDiscoveryZookeeperIpFinder();
        zooConfig.setZkConnectionString("192.168.2.8:2181");
        spi.setIpFinder(zooConfig);
        cfg.setDiscoverySpi(spi);
        TcpCommunicationSpi commSpi = new TcpCommunicationSpi();
        commSpi.setSlowClientQueueLimit(1000);

        cfg.setCommunicationSpi(commSpi);

        return cfg;
    }
}
