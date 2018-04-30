package ru.ulmc.bank.calculator.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.math.BigDecimal;

public class BasePriceSerializer extends Serializer<BasePrice> {
    @Override
    public void write(Kryo kryo, Output output, BasePrice object) {
        output.writeLong(object.getId());
        output.writeInt(object.getVolume());
        kryo.writeObject(output, object.getBid());
        kryo.writeObject(output, object.getOffer());
    }

    @Override
    public BasePrice read(Kryo kryo, Input input, Class<BasePrice> type) {
        BasePrice ce = kryo.newInstance(type);
        kryo.reference(ce);
        ce.setId(input.readLong());
        ce.setVolume(input.readInt());
        ce.setBid(kryo.readObject(input, BigDecimal.class));
        ce.setOffer(kryo.readObject(input, BigDecimal.class));
        return ce;
    }
}
