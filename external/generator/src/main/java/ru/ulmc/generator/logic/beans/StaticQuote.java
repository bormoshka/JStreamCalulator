package ru.ulmc.generator.logic.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class StaticQuote {
    private LocalDateTime dateTime;
    private double price;

    public StaticQuote(double price) {
        this.price = price;
    }
}
