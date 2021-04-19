package com.cy.tradingbot.domain;

import com.cy.tradingbot.component.TimeCalculator;
import com.cy.tradingbot.dto.CoinDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Coin {
    @JsonProperty("coin")
    private String coinName;

    private Wallet coinWallet;

    private double krwBalance;
    private int numOfCoinList;

    private double targetPrice;
    private double currentPrice;

    private List<Double> movingAverageList;
    private List<Candle> candles;

    @Builder
    public Coin(String coinName, Wallet coinWallet, double krwBalance, int numOfCoinList, double targetPrice, double currentPrice, List<Double> movingAverageList, List<Candle> candles) {
        this.coinName = coinName;
        this.coinWallet = coinWallet;
        this.krwBalance = krwBalance;
        this.numOfCoinList = numOfCoinList;
        this.targetPrice = targetPrice;
        this.currentPrice = currentPrice;
        this.movingAverageList = movingAverageList;
        this.candles = candles;
    }



    public OrderSheet createBuyingOrderSheet(double investment) {
        return OrderSheet.builder()
                .coinName(coinName)
                .side("bid")
                .price(investment)
                .orderType("price")
                .build();
    }

    public OrderSheet createSellOrderSheet() {
        return OrderSheet.builder()
                .coinName(coinName)
                .side("ask")
                .volume(coinWallet.getBalance())
                .orderType("market")
                .build();
    }

    public void setMovingAverageList(List<Double> movingAverageList) {
        this.movingAverageList = movingAverageList;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public boolean hasNotCoinBalance() {
        if (coinWallet == null)
            return true;

        return coinWallet.getBalance() <= 0;
    }

    public boolean didBuyIt() {
        return !hasNotCoinBalance();
    }

    public boolean isNotBreakthrough() {
        return currentPrice <= targetPrice;
    }

    public double getInvestment() {
        final double fee = 0.9994;

        return krwBalance * getScoreOfMovingAverage() * getVolatility() * fee;
    }

    private double getScoreOfMovingAverage() {
        double average = 0;
        for (Double ma : movingAverageList) {
            if (ma < currentPrice) average++;
        }

        average /= movingAverageList.size();
        return average;
    }

    private double getVolatility() {
        return 1 - (0.02 / getYesterdayVolatility() / numOfCoinList);
    }

    private double getYesterdayVolatility() {
        Candle candle = candles.get(1);
        return Math.abs(candle.getHighPrice() - candle.getLowPrice()) / currentPrice;
    }

    public CoinDTO toDTO() {
        CoinDTO coinDTO = new CoinDTO();
        coinDTO.setName(coinName.substring(4));

        coinDTO.setCurrentPrice(String.format("%,.3f", currentPrice));
        coinDTO.setTargetPrice(String.format("%,.3f", targetPrice));
        if (coinWallet != null)
            coinDTO.setPurchasePrice(String.format("%,.3f", coinWallet.getAvgBuyPrice()));

        coinDTO.setDateTime(TimeCalculator.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        return coinDTO;
    }

    public static Coin of(Wallet coinWallet, String coinName, int numOfCoinList) {
        return Coin.builder()
                .coinName("KRW-" + coinName)
                .coinWallet(coinWallet)
                .numOfCoinList(numOfCoinList)
                .build();
    }
}
