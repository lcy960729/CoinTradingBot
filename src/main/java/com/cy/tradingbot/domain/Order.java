package com.cy.tradingbot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    @JsonProperty("state")
    private String state;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("volume")
    private Double volume;
    @JsonProperty("uuid")
    private String uuid;

    public boolean isWait() {
        return state.equals("wait") || state.equals("watch");
    }
}
