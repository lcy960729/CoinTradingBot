package com.cy.tradingbot.services.coin;

import com.cy.tradingbot.domain.Coin;
import com.cy.tradingbot.services.log.LogService;
import com.cy.tradingbot.services.order.RequestOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellCoinService {
    @Autowired
    private RequestOrderService requestOrderService;

    @Autowired
    private LogService logService;

    public void sell(Coin coin) {
        if (coin.hasNotCoinBalance()) return;

        requestOrderService.requestOrder(coin.createSellOrderSheet());

        clearCoinWallet(coin);

        writeLog(coin.getCoinName());
    }

    private void clearCoinWallet(Coin coin) {
        coin.setCoinWallet(null);
    }

    private void writeLog(String coinName) {
        logService.write("[" + coinName + "] 시장가 매도 완료");
    }
}
