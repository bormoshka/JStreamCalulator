package ru.ulmc.bank.calculator.serialization;

import org.apache.flink.streaming.util.serialization.AbstractDeserializationSchema;
import ru.ulmc.bank.bean.IBaseQuote;
import ru.ulmc.bank.core.serialization.CommonJsonSerializer;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.io.IOException;

public class QuoteJsonSerializationSchema extends AbstractDeserializationSchema<IBaseQuote> {
    private static CommonJsonSerializer<BaseQuote> serializer = new CommonJsonSerializer<>(BaseQuote.class);

    @Override
    public IBaseQuote deserialize(byte[] message) throws IOException {
        return serializer.deserialize(message);
    }

}
