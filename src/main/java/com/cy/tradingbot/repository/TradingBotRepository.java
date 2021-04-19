package com.cy.tradingbot.repository;

import com.cy.tradingbot.domain.TradingBot;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TradingBotRepository {
    private final Map<String, TradingBot> tradingBotMap = new Hashtable<>();

    public TradingBot getTradingBot(String userName) {
        return tradingBotMap.get(userName);
    }

    public List<TradingBot> getTradingBots( ) {
        return new ArrayList<>(tradingBotMap.values());
    }

    public void createTradingBot(String userName, TradingBot tradingBot) {
        tradingBotMap.put(userName, tradingBot);
    }

    public void deleteTradingBot(String userName) {
        tradingBotMap.remove(userName);
    }

    public boolean isExist(String userName){
        return tradingBotMap.containsKey(userName);
    }
}
