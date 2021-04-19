package com.cy.tradingbot.domain;

import com.cy.tradingbot.component.TimeCalculator;
import com.cy.tradingbot.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class TradingBot {

    private List<Coin> coinList;
    private LocalDateTime closingTime;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private double krwBalance;

    public void updateKrwBalance(double krwBalance) {
        this.krwBalance = krwBalance;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public boolean hasNotKRWBalance() {
        return krwBalance <= 0;
    }

    public double getKrwBalance() {
        return krwBalance;
    }

    public static TradingBot of(Hashtable<String, Wallet> walletHashtable, List<String> coinNameList) {
        TradingBot tradingBot = new TradingBot();

        tradingBot.krwBalance = walletHashtable.get("KRW").getBalance();

        tradingBot.coinList = coinNameList.stream()
                .map(coinName -> Coin.of(walletHashtable.get(coinName), coinName, coinNameList.size()))
                .collect(Collectors.toList());

        tradingBot.updateClosingTime();
        tradingBot.updateKrwBalanceForEachCoin();

        return tradingBot;
    }

    public void updateKrwBalanceForEachCoin(){
        long numOfPurchasedCoin = coinList.stream().filter(Coin::didBuyIt).count();

        final double krwBalanceForEachCoin = krwBalance / (coinList.size() - numOfPurchasedCoin);

        for (Coin coin : coinList) {
            coin.setKrwBalance(krwBalanceForEachCoin);
        }
    }

    public List<Coin> getCoinList() {
        return coinList;
    }

    public boolean isClosingTime() {
        final LocalDateTime now = TimeCalculator.now();

        return now.isBefore(closingTime.plusMinutes(1)) && now.isAfter(closingTime);
    }

    public void updateClosingTime() {
        closingTime = TimeCalculator.closingTime();
    }
}
