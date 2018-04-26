package ru.ulmc.generator.logic;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import ru.ulmc.generator.logic.beans.AppConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AppConfigurationSerializer extends Serializer<AppConfiguration> {
    @Override
    public void write(Kryo kryo, Output output, AppConfiguration object) {
        kryo.writeClassAndObject(output, object.getProperties());
        kryo.writeClassAndObject(output, object.getRecentFiles().stream().map(File::getAbsolutePath).collect(Collectors.toSet()));

    }

    @Override
    @SuppressWarnings("unchecked")
    public AppConfiguration read(Kryo kryo, Input input, Class<AppConfiguration> type) {
        AppConfiguration ce = kryo.newInstance(type);
        kryo.reference(ce);
        ce.getProperties().putAll((Map<? extends String, ? extends String>) kryo.readClassAndObject(input));
        ce.getRecentFiles().addAll(((Set<String>)kryo.readClassAndObject(input)).stream().map(File::new)
                .collect(Collectors.toSet()));
        return ce;
    }
}
