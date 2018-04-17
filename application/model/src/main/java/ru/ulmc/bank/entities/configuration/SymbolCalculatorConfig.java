package ru.ulmc.bank.entities.configuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(of = "fullClassname")
public class SymbolCalculatorConfig implements Serializable {
    private String fullClassname;
    private double modifier;

}
