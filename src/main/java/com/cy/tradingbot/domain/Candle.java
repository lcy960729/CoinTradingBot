package com.cy.tradingbot.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Candle {
    @JsonProperty("trade_price")
    private Double tradePrice;
    @JsonProperty("low_price")
    private Double lowPrice;
    @JsonProperty("high_price")
    private Double highPrice;
    @JsonProperty("opening_price")
    private Double openingPrice;

    private boolean isIncreased() {
        return highPrice - lowPrice > 0;
    }

    public double calcKValue() {
        return isIncreased() ? (1 - (Math.abs(openingPrice - tradePrice) / (highPrice - lowPrice))) : 0;
    }
}
