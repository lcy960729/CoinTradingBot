package com.cy.tradingbot.services;

import com.cy.tradingbot.domain.Coin;
import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.dto.CoinDTO;
import com.cy.tradingbot.repository.TradingBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetTradingBotService {
    @Autowired
    private TradingBotRepository tradingBotRepository;

    public List<CoinDTO> getCoinInfoList(String userName) {
        TradingBot tradingBot = tradingBotRepository.getTradingBot(userName);

        return tradingBot.getCoinList().stream()
                .map(Coin::toDTO)
                .collect(Collectors.toList());
    }

    public String getClosingTIme(String userName) {
        TradingBot tradingBot = tradingBotRepository.getTradingBot(userName);
        return tradingBot.getClosingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }

    public List<TradingBot> getTradingBots(){
        return tradingBotRepository.getTradingBots();
    }

    public boolean isNotExist(String userName){
        return !isExist(userName);
    }

    public boolean isExist(String userName){
        return tradingBotRepository.isExist(userName);
    }
}
