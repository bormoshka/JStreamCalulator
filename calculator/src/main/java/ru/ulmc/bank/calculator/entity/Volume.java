package ru.ulmc.bank.calculator.entity;

import lombok.Data;
import ru.ulmc.bank.calculator.exception.ConfigurationException;

import java.io.Serializable;

@Data
public final class Volume implements Comparable<Volume>, Serializable {
    private int size;

    public Volume(int size) {
        if (size < 0) {
            throw new ConfigurationException("Volume size should be greater or equals to zero");
        }

        this.size = size;
    }

    public int compareTo(Volume o) {
        return Integer.compare(size, o.size);
    }
}
