package com.cy.tradingbot.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSheet {
    private String coinName;
    private String side;
    private Double volume;
    private Double price;
    private String orderType;

    @Builder
    public OrderSheet(String coinName, String side, Double volume, Double price, String orderType) {
        this.coinName = coinName;
        this.side = side;
        this.volume = volume;
        this.price = price;
        this.orderType = orderType;
    }
}
