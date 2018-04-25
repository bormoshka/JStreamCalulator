package ru.ulmc.bank.calculators.util;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ImplementingClassMatchProcessor;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.calculators.Calculator;
import ru.ulmc.bank.calculators.ResourcesEnvironment;
import ru.ulmc.bank.core.common.exception.FxException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CalculatorsLocator {
    private static final Map<String, Calculator> loadedCalculators = new ConcurrentHashMap<>();
    private static final Object[] monitor = {};
    private static Boolean scanned = false;

    public static void findAndInitCalculators(ResourcesEnvironment environment) {
        synchronized (monitor) {
            FastClasspathScanner scanner = getFastClasspathScanner(subclass -> {
                String name = subclass.getPackage().getName();
                log.debug("Loading calculator class {} from package: {}", subclass.getSimpleName(), name);
                loadedCalculators.put(subclass.getSimpleName(), init(environment, subclass));
            });
            scanner.scan();
            scanned = true;
        }
    }

    public static Set<CalculatorInfo> collect() {

        Set<CalculatorInfo> loadedCalculatorsClasses = new HashSet<>();
        FastClasspathScanner scanner = getFastClasspathScanner(subclass -> {
            String name = subclass.getPackage().getName();
            log.debug("Loading calculator class {} from package: {}", subclass.getSimpleName(), name);
            CalcPlugin pluginInfo = subclass.getDeclaredAnnotation(CalcPlugin.class);
            if (pluginInfo != null) {
                loadedCalculatorsClasses.add(new CalculatorInfo(pluginInfo.name(), pluginInfo.description(),
                        subclass.getSimpleName(), subclass.getCanonicalName()));
            }
        });
        scanner.scan();
        return loadedCalculatorsClasses;
    }

    private static FastClasspathScanner getFastClasspathScanner(
            ImplementingClassMatchProcessor<Calculator> interfaceMatchProcessor) {
        FastClasspathScanner scanner = new FastClasspathScanner("ru.ulmc.bank.calculators");
        scanner.addClassLoader(CalculatorsLocator.class.getClassLoader());
        scanner.matchClassesImplementing(Calculator.class, interfaceMatchProcessor);
        return scanner;
    }

    public static Map<String, Calculator> getAvailableCalculators() {
        if (scanned) {
            return loadedCalculators;
        }
        throw new FxException("Uninitialized locator call!");
    }


    private static Calculator init(ResourcesEnvironment environment, Class<? extends Calculator> subclass) {
        try {
            return subclass.newInstance().initialize(environment);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Calculator instance creation failed", e);
            throw new FxException("Calculator initialization failed!", e);
        }
    }
}
