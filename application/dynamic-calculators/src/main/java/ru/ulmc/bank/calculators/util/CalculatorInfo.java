package ru.ulmc.bank.calculators.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CalculatorInfo {
    private final String name;
    private final String description;
    private final String className;
    private final String fullClassName;
}
