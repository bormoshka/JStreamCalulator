package ru.ulmc.bank.calculator.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ru.ulmc.bank.entities.persistent.financial.BasePrice;
import ru.ulmc.bank.entities.persistent.financial.BaseQuote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BaseQuoteSerializer extends Serializer<BaseQuote> {
    @Override
    public void write(Kryo kryo, Output output, BaseQuote object) {
        output.writeString(object.getId());
        output.writeString(object.getSymbol());
        kryo.writeObject(output, object.getDatetime());
        kryo.writeObject(output, object.getReceiveTime());
        output.writeInt(object.getPrices().size());
        object.getPrices().forEach(p -> kryo.writeObject(output, p));
    }

    @Override
    public BaseQuote read(Kryo kryo, Input input, Class<BaseQuote> type) {
        BaseQuote ce = kryo.newInstance(type);
        kryo.reference(ce);
        ce.setId(input.readString());
        ce.setSymbol(input.readString());
        ce.setDatetime(kryo.readObject(input, LocalDateTime.class));
        ce.setReceiveTime(kryo.readObject(input, LocalDateTime.class));
        int size  = input.readInt();
        Set<BasePrice> prices = new HashSet<>();
        while (size-- > 0) {
            prices.add(kryo.readObject(input, BasePrice.class));
        }
        ce.setPrices(prices);
        return ce;
    }
}
