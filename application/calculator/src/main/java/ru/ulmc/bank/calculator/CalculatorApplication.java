package ru.ulmc.bank.calculator;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.codehaus.jackson.map.Serializers;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.environment.ResourcesEnvironment;
import ru.ulmc.bank.calculator.environment.impl.DefaultResourcesEnvironment;
import ru.ulmc.bank.calculator.serialization.QuoteJsonSerializationSchema;
import ru.ulmc.bank.calculator.service.processors.DefaultProcessor;
import ru.ulmc.bank.constants.Queues;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

@Slf4j
public class CalculatorApplication {


    public static void main(String[] args) throws Exception {

        Cli cli = new Cli(args);
        EnvironmentHolder.init(cli.getZookeeperConnectString());
        DefaultProcessor processor = new DefaultProcessor();
        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(30000L);
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(cli.getMqHost())
                .setPort(cli.getMqPort())
                .setUserName(cli.getMqUsername())
                .setPassword(cli.getMqPassword())
                .setVirtualHost("/")
                .build();

        final DataStream<IBaseQuote> stream = env
                .addSource(new RMQSource<>(
                        connectionConfig,            // config for the RabbitMQ connection
                        Queues.BASE_QUOTES_QUEUE,                 // name of the RabbitMQ queue to consume
                        false,                        // use correlation ids; can be false if only at-least-once is required
                        new QuoteJsonSerializationSchema()))   // deserialization schema to turn messages into Java objects
                .setParallelism(1);              // non-parallel source is only required for exactly-once
        stream.addSink(baseQuote -> log.info("Got a quote {}", baseQuote));
        stream.process(processor)
                .addSink(quote -> log.info("CalculatedQuote: {}", quote));
       // stream.addSink(new RMQSink<>(
       //         connectionConfig,
       //         Queues.BASE_QUOTES_REPLY,
       //         new TypeInformationSerializationSchema<>(TypeInformation.of(IBaseQuote.class), env.getConfig())));
        env.execute("Rabbit MQ CalculatorApplication");
    }
}
