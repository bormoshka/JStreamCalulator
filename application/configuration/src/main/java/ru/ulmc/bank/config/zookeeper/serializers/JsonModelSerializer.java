package ru.ulmc.bank.config.zookeeper.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.apache.curator.x.async.modeled.ModelSerializer;

import java.io.IOException;
import java.util.Arrays;

public class JsonModelSerializer<T> implements ModelSerializer<T> {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private final ObjectReader reader;
    private final ObjectWriter writer;

    public JsonModelSerializer(Class<T> modelClass) {
        this(mapper.getTypeFactory().constructType(modelClass));
    }

    private JsonModelSerializer(JavaType type) {
        reader = mapper.readerFor(type);
        writer = mapper.writerFor(type);
    }

    public static <T> JsonModelSerializer<T> build(Class<T> modelClass) {
        return new JsonModelSerializer<>(modelClass);
    }

    @Override
    public byte[] serialize(T model) {
        try {
            return writer.writeValueAsBytes(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Could not serialize value: %s", model), e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        try {
            return reader.readValue(bytes);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not deserialize value: %s", Arrays.toString(bytes)), e);
        }
    }
}
