package com.cy.tradingbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class TradingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingBotApplication.class, args);
    }
}
