package ru.ulmc.bank.config.zookeeper.entities;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "fullClassname")
public class SymbolCalculatorConfig implements Serializable {
    private String fullClassname;
    private double bidModifier;
    private double offerModifier;

}
