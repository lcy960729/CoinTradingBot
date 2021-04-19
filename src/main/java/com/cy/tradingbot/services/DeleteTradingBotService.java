package com.cy.tradingbot.services;

import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.repository.TradingBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class DeleteTradingBotService {
    @Autowired
    private TradingBotRepository tradingBotRepository;

    private final Queue<String> garbageTradingBots = new LinkedList<>();

    public void delete(String userName) {
        garbageTradingBots.offer(userName);
    }

    public void emptyAllGarbage() {
        while (!garbageTradingBots.isEmpty()) {
            tradingBotRepository.deleteTradingBot(garbageTradingBots.poll());
        }
    }

}
