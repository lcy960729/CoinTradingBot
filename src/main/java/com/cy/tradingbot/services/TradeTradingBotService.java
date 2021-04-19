package com.cy.tradingbot.services;

import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Coin;
import com.cy.tradingbot.domain.TradingBot;
import com.cy.tradingbot.services.coin.BuyCoinService;
import com.cy.tradingbot.services.coin.SellCoinService;
import com.cy.tradingbot.services.coin.UpdateTargetPriceAndMovingAverageForCoinService;
import com.cy.tradingbot.services.log.LogService;
import com.cy.tradingbot.services.log.RecordService;
import com.cy.tradingbot.services.wallet.GetWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeTradingBotService {
    @Autowired
    private LogService logService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private GetWalletService getWalletService;
    @Autowired
    private UpdateTargetPriceAndMovingAverageForCoinService updateTargetPriceAndMovingAverageForCoinService;
    @Autowired
    private BuyCoinService buyCoinService;
    @Autowired
    private SellCoinService sellCoinService;

    @Autowired
    private UpBitAPI upBitAPI;

    public void trade(TradingBot tradingBot) throws InterruptedException {
        upBitAPI.setAccessKey(tradingBot.getUser().getAccessKey());
        upBitAPI.setSecretKey(tradingBot.getUser().getSecretKey());

        if (tradingBot.isClosingTime()) {
            closingChapter(tradingBot);

            openChapter(tradingBot);
            return;
        }

        if (tradingBot.hasNotKRWBalance()) return;

        tradingBot.getCoinList().forEach(coin -> buyCoinService.buy(coin));
    }

    private void openChapter(TradingBot tradingBot) {
        logService.write("### 장 시작 ###");

        tradingBot.updateClosingTime();
        tradingBot.updateKrwBalance(getWalletService.getKrwWallet().getBalance());

        tradingBot.updateKrwBalanceForEachCoin();

        for (Coin coin : tradingBot.getCoinList()) {
            updateTargetPriceAndMovingAverageForCoinService.update(tradingBot.getUser(), coin);
        }
    }

    private void closingChapter(TradingBot tradingBot) {
        logService.write("### 장 마감 ###");

        tradingBot.getCoinList()
                .forEach(coin -> sellCoinService.sell(coin));

        recordService.write(tradingBot.getKrwBalance());
    }
}
