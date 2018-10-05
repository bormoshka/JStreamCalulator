package ru.ulmc.bank.config.zookeeper.entities;

import lombok.*;

/**
 * Валюта.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "iso")
public class Currency {

    private String iso;

    private String name;

    private boolean isActive;


    public Currency() {
    }

    public Currency(String iso, boolean isActive) {
        this.iso = iso;
        this.isActive = isActive;
    }

}
