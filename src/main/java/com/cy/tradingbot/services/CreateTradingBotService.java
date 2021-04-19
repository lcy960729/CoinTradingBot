package com.cy.tradingbot.services;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.domain.Wallet;
import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.repository.TradingBotRepository;
import com.cy.tradingbot.services.coin.UpdateTargetPriceAndMovingAverageForCoinService;
import com.cy.tradingbot.services.wallet.GetWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

@Service
public class CreateTradingBotService {
    @Autowired
    private TradingBotRepository tradingBotRepository;

    @Autowired
    private GetWalletService getWalletService;
    @Autowired
    private UpdateTargetPriceAndMovingAverageForCoinService updateTargetPriceAndMovingAverageForCoinService;

    @Autowired
    private UpBitAPI upBitAPI;

    public void create(User user) {
        upBitAPI.setAccessKey(user.getAccessKey());
        upBitAPI.setSecretKey(user.getSecretKey());

        Hashtable<String, Wallet> walletHashtable = getWalletService.getWalletHashTable();

        TradingBot tradingBot = TradingBot.of(walletHashtable, user.getCoinList());
        tradingBot.setUser(user);

        tradingBot.getCoinList().forEach(coin -> {
            updateTargetPriceAndMovingAverageForCoinService.update(tradingBot.getUser(), coin);
        });

        tradingBotRepository.createTradingBot(user.getUserName(), tradingBot);
    }
}
