package com.cy.tradingbot.services.coin;

import com.cy.tradingbot.domain.Coin;
import com.cy.tradingbot.domain.Wallet;
import com.cy.tradingbot.services.wallet.GetWalletService;
import com.cy.tradingbot.services.candle.GetTickerService;
import com.cy.tradingbot.services.log.LogService;
import com.cy.tradingbot.services.order.RequestOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuyCoinService {
    @Autowired
    private LogService logService;
    @Autowired
    private GetWalletService getWalletService;
    @Autowired
    private GetTickerService getTickerService;
    @Autowired
    private RequestOrderService requestOrderService;

    public void buy(Coin coin) {
        updateCurrentPrice(coin);

        double investment = coin.getInvestment();

        if (coin.isNotBreakthrough() || coin.didBuyIt() || investment <= 5000)
            return;

        requestOrderService.requestOrder(coin.createBuyingOrderSheet(investment));

        updateBalanceOfCoinWallet(coin);

        writeLog(coin.getCoinName());
    }

    private void updateCurrentPrice(Coin coin) {
        coin.setCurrentPrice(getCurrentPrice(coin.getCoinName()));
    }

    private void updateBalanceOfCoinWallet(Coin coin) {
        Wallet coinWallet = getWalletService.getCoinWallet(coin.getCoinName().substring(4));
        coin.setCoinWallet(coinWallet);
    }

    private void writeLog(String coinName) {
        logService.write("[" + coinName + "] 시장가 매수 완료");
    }

    private double getCurrentPrice(String coinName) {
        return getTickerService.getTicker(coinName).getTradePrice();
    }
}
