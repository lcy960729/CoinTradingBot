package com.cy.tradingbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecordDTO {
    private String dateTime;
    private String balance;
    private String yield;

    public void setYield(Double yield) {
        this.yield = String.format("%,.2f", yield) + "%";
    }
}
