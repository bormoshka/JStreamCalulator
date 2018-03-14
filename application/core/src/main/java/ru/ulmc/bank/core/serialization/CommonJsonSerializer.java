package ru.ulmc.bank.core.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.util.Arrays;

public class CommonJsonSerializer<T> {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    private final ObjectReader reader;
    private final ObjectWriter writer;

    public CommonJsonSerializer(Class<T> modelClass) {
        this(mapper.getTypeFactory().constructType(modelClass));
    }

    private CommonJsonSerializer(JavaType type) {
        reader = mapper.readerFor(type);
        writer = mapper.writerFor(type);
    }

    public byte[] serialize(T model) {
        try {
            return writer.writeValueAsBytes(model);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Could not serialize value: %s", model), e);
        }
    }

    public T deserialize(byte[] bytes) {
        try {
            return reader.readValue(bytes);
        } catch (IOException e) {

            throw new RuntimeException(String.format("Could not deserialize value: %s", Arrays.toString(bytes)), e);
        }
    }
}
