package com.cy.tradingbot.dto;

import com.cy.tradingbot.domain.Coin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CoinDTO {
    private String name;
    private String currentPrice;
    private String targetPrice;
    private String purchasePrice;
    private String dateTime;
}
