package ru.ulmc.bank.calculator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Cli {
    private String[] args = null;
    private Options options = new Options();
    @Getter
    private String zookeeperConnectString;
    @Getter
    private String mqHost;
    @Getter
    private int mqPort;
    @Getter
    private String mqUsername;
    @Getter
    private String mqPassword;

    public Cli(String[] args) {
        this.args = args;

        options.addOption("h", "help", false, "Shows help");
        options.addRequiredOption("z", "zookeeper-url", true, "Zookeeper connection URL");
        options.addRequiredOption("mqh", "rmq-host", true, "RabbitMQ host name (ip)");
        options.addRequiredOption("mqp", "rmq-port", true, "RabbitMQ port");
        options.addRequiredOption("mqu", "rmq-username", true, "RabbitMQ username");
        options.addRequiredOption("mqpw", "rmq-password", true, "RabbitMQ password");

        parse();
    }

    private void parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
            }

            zookeeperConnectString = cmd.getOptionValue("z");
            mqHost = cmd.getOptionValue("mqh");
            mqPort = Integer.parseInt(cmd.getOptionValue("mqp"));
            mqUsername = cmd.getOptionValue("mqu");
            mqPassword = cmd.getOptionValue("mqpw");
        } catch (ParseException e) {
            log.error("Failed to parse command line properties", e);
            help();
        }
    }

    private void error() {
        log.error("Failed to parse command line properties");
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Main", options);
        System.exit(0);
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("Main", options);
        System.exit(0);
    }
}
