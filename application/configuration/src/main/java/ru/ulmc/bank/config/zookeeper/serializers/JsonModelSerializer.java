package ru.ulmc.bank.config.zookeeper.serializers;

import org.apache.curator.x.async.modeled.ModelSerializer;
import ru.ulmc.bank.core.serialization.CommonJsonSerializer;

public class JsonModelSerializer<T> implements ModelSerializer<T> {
    private final CommonJsonSerializer<T> jsonSerializer;

    public JsonModelSerializer(Class<T> modelClass) {
        jsonSerializer = new CommonJsonSerializer<>(modelClass);
    }

    public static <T> JsonModelSerializer<T> build(Class<T> modelClass) {
        return new JsonModelSerializer<>(modelClass);
    }

    @Override
    public byte[] serialize(T model) {
        return jsonSerializer.serialize(model);
    }

    @Override
    public T deserialize(byte[] bytes) {
        return jsonSerializer.deserialize(bytes);
    }
}
