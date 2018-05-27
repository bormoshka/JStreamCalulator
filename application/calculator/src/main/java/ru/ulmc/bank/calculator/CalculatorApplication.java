package ru.ulmc.bank.calculator;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.BaseAlignedWindowAssigner;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.streaming.util.serialization.TypeInformationSerializationSchema;
import ru.ulmc.bank.calculator.environment.EnvironmentHolder;
import ru.ulmc.bank.calculator.serialization.BasePriceSerializer;
import ru.ulmc.bank.calculator.serialization.BaseQuoteDto;
import ru.ulmc.bank.calculator.serialization.BaseQuoteSerializer;
import ru.ulmc.bank.calculator.serialization.QuoteJsonSerializationSchema;
import ru.ulmc.bank.calculator.service.processors.BaseQuoteProcessor;
import ru.ulmc.bank.calculator.service.processors.CalculationProcessor;
import ru.ulmc.bank.calculator.service.processors.DefaultProcessor;
import ru.ulmc.bank.calculator.service.processors.PublishingProcessor;
import ru.ulmc.bank.calculators.util.CalculatorsLocator;
import ru.ulmc.bank.constants.Queues;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;
import ru.ulmc.bank.entities.persistent.financial.Quote;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;

@Slf4j
public class CalculatorApplication {
    private static final LocalDateTime startTime = LocalDateTime.now();
    public static void main(String[] args) throws Exception {
        Cli cli = new Cli(args);

        EnvironmentHolder.initResources(cli.getZookeeperConnectString());
        CalculatorsLocator.findAndInitCalculators(EnvironmentHolder.getResources());
        EnvironmentHolder.initServices(CalculatorsLocator.getAvailableCalculators());

        BaseQuoteProcessor baseQuoteProcessor = new BaseQuoteProcessor();
        CalculationProcessor calcProcessor = new CalculationProcessor();
        DefaultProcessor processor = new DefaultProcessor();
        PublishingProcessor publisher = new PublishingProcessor();

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(30000L);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().addDefaultKryoSerializer(BaseQuote.class, BaseQuoteSerializer.class);
        env.getConfig().addDefaultKryoSerializer(BasePrice.class, BasePriceSerializer.class);
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));

        final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(cli.getMqHost())
                .setPort(cli.getMqPort())
                .setUserName(cli.getMqUsername())
                .setPassword(cli.getMqPassword())
                .setVirtualHost("/")
                .build();

        DataStream<BaseQuoteDto> stream = env
                .addSource(new RMQSource<>(
                        connectionConfig,
                        Queues.BASE_QUOTES_QUEUE,
                        false,
                        new QuoteJsonSerializationSchema()))
                .setMaxParallelism(8)
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<BaseQuoteDto>() {
                    @Override
                    public long extractAscendingTimestamp(BaseQuoteDto element) {
                        return java.util.Date
                                .from(element.getDatetime().atZone(ZoneId.systemDefault())
                                        .toInstant()).getTime();
                    }
                })
                .keyBy("symbol");
        int windowingTime = Integer.parseInt(EnvironmentHolder.getResources().getAppConfigStorage().getProperties().getOrDefault("flink.window.interval", "0"));
        if (windowingTime > 10) {
            stream = ((KeyedStream) stream).window(TumblingEventTimeWindows.of(Time.milliseconds(windowingTime)))
                    .allowedLateness(Time.milliseconds(10)).maxBy("datetime");
        }
        stream.process(baseQuoteProcessor)
                .process(calcProcessor)
                .process(processor)
                .process(publisher)
                .addSink(new RMQSink<>(
                        connectionConfig,
                        Queues.BASE_QUOTES_REPLY,
                        new TypeInformationSerializationSchema<>(TypeInformation.of(Quote.class), env.getConfig())));
        env.execute("Rabbit MQ CalculatorApplication");

    }
}
