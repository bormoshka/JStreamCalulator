package ru.ulmc.generator.logic.beans;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private String symbol;
    private double bid;
    private double offer;
    private double volatility;
    private double interval;

    private List<StreamTask> streamTasks;

    public static UserConfiguration.ConfigurationBuilder builder() {
        return new UserConfiguration.ConfigurationBuilder();
    }

    public static class ConfigurationBuilder {
        private String symbol;
        private double bid;
        private double offer;
        private double volatility;
        private double interval;
        private List<StreamTask> streamTasks;

        ConfigurationBuilder() {
        }

        public UserConfiguration.ConfigurationBuilder symbol(final String symbol) {
            this.symbol = symbol;
            return this;
        }

        public UserConfiguration.ConfigurationBuilder bid(final double bid) {
            this.bid = bid;
            return this;
        }

        public UserConfiguration.ConfigurationBuilder offer(final double offer) {
            this.offer = offer;
            return this;
        }

        public UserConfiguration.ConfigurationBuilder volatility(final double volatility) {
            this.volatility = volatility;
            return this;
        }

        public UserConfiguration.ConfigurationBuilder interval(final double interval) {
            this.interval = interval;
            return this;
        }

        public UserConfiguration.ConfigurationBuilder streamTasks(final List<StreamTask> streamTasks) {
            this.streamTasks = streamTasks;
            return this;
        }

        public UserConfiguration build() {
            return new UserConfiguration(this.symbol, this.bid, this.offer, this.volatility, this.interval, this.streamTasks);
        }

        public String toString() {
            return "UserConfiguration.ConfigurationBuilder(symbol=" + this.symbol + ", bid=" + this.bid + ", offer=" + this.offer + ", volatility=" + this.volatility + ", interval=" + this.interval + ", streamTasks=" + this.streamTasks + ")";
        }
    }

}
