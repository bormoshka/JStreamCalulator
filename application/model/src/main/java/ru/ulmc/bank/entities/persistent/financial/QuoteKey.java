package ru.ulmc.bank.entities.persistent.financial;

import lombok.AllArgsConstructor;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
public class QuoteKey {
    @AffinityKeyMapped
    private String symbol;
    private String quoteUUID;
    private LocalDateTime dateTime;

    public QuoteKey(String symbol) {
        this.symbol = symbol;
        dateTime = LocalDateTime.now();
        quoteUUID = UUID.randomUUID().toString();
    }
}
