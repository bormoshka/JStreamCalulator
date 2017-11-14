package ru.ulmc.bank.config.zookeeper.serializers;

import org.apache.curator.x.async.modeled.ModelSerializer;

public class StringModelSerializer implements ModelSerializer<String> {

    public StringModelSerializer() {
    }

    @Override
    public byte[] serialize(String model) {
        return model != null ? model.getBytes() : null;
    }

    @Override
    public String deserialize(byte[] bytes) {
        return bytes != null ? new String(bytes) : null;
    }
}
