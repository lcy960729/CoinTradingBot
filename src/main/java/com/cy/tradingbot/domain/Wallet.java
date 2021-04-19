package com.cy.tradingbot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wallet {
    @JsonProperty("avg_buy_price")
    private Double avgBuyPrice;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("currency")
    private String currency;
}
